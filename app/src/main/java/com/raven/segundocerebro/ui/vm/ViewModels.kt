package com.raven.segundocerebro.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.raven.segundocerebro.SegundoCerebroApp
import com.raven.segundocerebro.data.Container
import com.raven.segundocerebro.data.Note
import com.raven.segundocerebro.data.ParaType
import com.raven.segundocerebro.data.Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Application.repo: Repository get() = (this as SegundoCerebroApp).repository

private fun <T> AndroidViewModel.state(flow: kotlinx.coroutines.flow.Flow<T>, initial: T): StateFlow<T> =
    flow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = app.repo

    val inboxCount = state(repo.inboxCount(), 0)
    val recent = state(repo.recent(8), emptyList<Note>())

    // Contador P.A.R.A. = nº de contenedores por tipo (reactivo).
    val counts: StateFlow<Map<ParaType, Int>> = state(
        combine(ParaType.organizable.map { t -> repo.containerCountByType(t).map { t to it } }) { it.toMap() },
        emptyMap()
    )

    fun exportTo(uri: android.net.Uri, done: (String) -> Unit) = viewModelScope.launch {
        val r = com.raven.segundocerebro.data.BackupManager.export(getApplication(), uri, repo)
        done(r.fold({ "Exportadas $it notas" }, { "Error al exportar" }))
    }

    fun importFrom(uri: android.net.Uri, done: (String) -> Unit) = viewModelScope.launch {
        val r = com.raven.segundocerebro.data.BackupManager.import(getApplication(), uri, repo)
        done(r.fold({ "Restauradas $it notas" }, { "Error al importar" }))
    }
}

class InboxViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = app.repo
    val notes = state(repo.inbox(), emptyList<Note>())
    val containers = state(repo.allContainers(), emptyList<Container>())

    fun move(note: Note, container: Container?) = viewModelScope.launch { repo.moveNote(note, container) }
    fun delete(note: Note) = viewModelScope.launch { repo.deleteNote(note) }
    fun capture(text: String) = viewModelScope.launch { if (text.isNotBlank()) repo.quickCapture(text) }
}

class ParaListViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = app.repo
    val type: ParaType = runCatching { ParaType.valueOf(handle.get<String>("type") ?: "RESOURCE") }
        .getOrDefault(ParaType.RESOURCE)

    val containers = state(repo.containersOf(type), emptyList<Container>())

    fun create(name: String, description: String) = viewModelScope.launch {
        if (name.isNotBlank()) repo.saveContainer(Container(name = name.trim(), type = type, description = description.trim()))
    }
    fun delete(c: Container) = viewModelScope.launch { repo.deleteContainer(c) }
}

class ContainerViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = app.repo
    val id: String = handle.get<String>("id") ?: ""

    val container = state(repo.container(id), null as Container?)
    val notes = state(repo.notesIn(id), emptyList<Note>())

    fun delete(note: Note) = viewModelScope.launch { repo.deleteNote(note) }
}

class EditorViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = app.repo
    private val noteId: String = handle.get<String>("noteId").orEmpty()
    private val preContainer: String = handle.get<String>("containerId").orEmpty()

    val isNew = noteId.isBlank()
    val note = state(if (isNew) flowOf(null) else repo.note(noteId), null as Note?)
    val containers = state(repo.allContainers(), emptyList<Container>())
    val initialContainerId: String? = preContainer.ifBlank { null }

    fun save(draft: Note) = viewModelScope.launch { repo.saveNote(draft) }
    fun delete(draft: Note) = viewModelScope.launch { repo.deleteNote(draft) }
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SearchViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = app.repo
    val queryState = androidx.compose.runtime.mutableStateOf("")

    val results: StateFlow<List<Note>> = androidx.compose.runtime.snapshotFlow { queryState.value }
        .flatMapLatest { text ->
            if (text.isBlank()) flowOf(emptyList()) else repo.search(text.trim())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
