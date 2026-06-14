package com.raven.segundocerebro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.segundocerebro.ui.components.EmptyState
import com.raven.segundocerebro.ui.components.NoteCard
import com.raven.segundocerebro.ui.vm.ContainerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerScreen(
    onBack: () -> Unit,
    onOpenNote: (String) -> Unit,
    onNewNote: (String) -> Unit,
    vm: ContainerViewModel = viewModel()
) {
    val container by vm.container.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            container?.name ?: "…",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        container?.let {
                            Text(
                                it.type.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewNote(vm.id) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Rounded.Add, contentDescription = "Nueva nota") }
        }
    ) { pad ->
        if (notes.isEmpty()) {
            Column(Modifier.padding(pad)) {
                EmptyState("Sin notas", "Añade la primera con el botón +.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(pad).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(note) { onOpenNote(note.id) }
                }
            }
        }
    }
}
