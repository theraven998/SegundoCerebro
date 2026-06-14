package com.raven.segundocerebro.data

import kotlinx.coroutines.flow.Flow

/** Punto único de acceso a datos. DI manual desde la Application. */
class Repository(
    private val notes: NoteDao,
    private val containers: ContainerDao
) {
    // ---- Notas ----
    fun recent(limit: Int = 8) = notes.recent(limit)
    fun inbox() = notes.inbox()
    fun inboxCount() = notes.inboxCount()
    fun countByType(type: ParaType) = notes.countByType(type)
    fun notesIn(containerId: String) = notes.byContainer(containerId)
    fun favorites() = notes.favorites()
    fun note(id: String) = notes.byId(id)
    fun search(q: String) = notes.search(q)

    suspend fun saveNote(note: Note) = notes.insert(note.copy(updatedAt = System.currentTimeMillis()))
    suspend fun deleteNote(note: Note) = notes.delete(note)

    /** Captura rápida: una nota a la bandeja con solo texto. */
    suspend fun quickCapture(text: String) {
        val trimmed = text.trim()
        val title = trimmed.lineSequence().first().take(80)
        val body = trimmed.substringAfter('\n', "").trim()
        notes.insert(Note(title = title, content = body, type = ParaType.INBOX))
    }

    /** Organizar (paso CODE): mover nota a un contenedor P.A.R.A. */
    suspend fun moveNote(note: Note, container: Container?) {
        notes.update(
            note.copy(
                containerId = container?.id,
                type = container?.type ?: ParaType.INBOX,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // ---- Contenedores ----
    fun containersOf(type: ParaType) = containers.byType(type)
    fun allContainers() = containers.all()
    fun container(id: String) = containers.byId(id)
    fun containerNoteCount(id: String) = containers.noteCount(id)
    fun containerCountByType(type: ParaType) = containers.countByType(type)

    suspend fun saveContainer(c: Container) = containers.upsert(c.copy(updatedAt = System.currentTimeMillis()))
    suspend fun deleteContainer(c: Container) = containers.delete(c)

    // ---- Backup ----
    suspend fun exportSnapshot(): BackupData =
        BackupData(notes = notes.allOnce(), containers = containers.allOnce())

    /** Restaura un backup: fusiona por id (reemplaza los que coincidan). */
    suspend fun importSnapshot(data: BackupData) {
        containers.upsertAll(data.containers)
        notes.insertAll(data.notes)
    }
}
