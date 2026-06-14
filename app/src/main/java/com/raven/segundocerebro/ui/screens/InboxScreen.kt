package com.raven.segundocerebro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.segundocerebro.data.Note
import com.raven.segundocerebro.ui.components.Dot
import com.raven.segundocerebro.ui.components.EmptyState
import com.raven.segundocerebro.ui.components.NoteCard
import com.raven.segundocerebro.ui.theme.accent
import com.raven.segundocerebro.ui.vm.InboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onBack: () -> Unit,
    onOpenNote: (String) -> Unit,
    vm: InboxViewModel = viewModel()
) {
    val notes by vm.notes.collectAsStateWithLifecycle()
    val containers by vm.containers.collectAsStateWithLifecycle()
    var capture by remember { mutableStateOf("") }
    var organizing by remember { mutableStateOf<Note?>(null) }
    val sheet = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Bandeja", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxWidth()) {
            OutlinedTextField(
                value = capture,
                onValueChange = { capture = it },
                placeholder = { Text("Captura una idea rápida…") },
                trailingIcon = {
                    IconButton(
                        onClick = { vm.capture(capture); capture = "" },
                        enabled = capture.isNotBlank()
                    ) { Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Guardar") }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)
            )
            if (notes.isEmpty()) {
                EmptyState("Bandeja vacía", "Captura aquí y organiza después, sin fricción.")
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp, 16.dp, 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            NoteCard(note) { onOpenNote(note.id) }
                            Row(Modifier.padding(start = 4.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    "Organizar",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickableText { organizing = note }
                                )
                                Text(
                                    "Eliminar",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.clickableText { vm.delete(note) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val target = organizing
    if (target != null) {
        ModalBottomSheet(
            onDismissRequest = { organizing = null },
            sheetState = sheet,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(20.dp, 0.dp, 20.dp, 32.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Mover a…", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Elige un contenedor P.A.R.A.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (containers.isEmpty()) {
                    Text(
                        "Aún no hay contenedores. Crea un Proyecto o Área desde la pantalla P.A.R.A.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                containers.forEach { c ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clickableText { vm.move(target, c); organizing = null }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Dot(c.type.accent(), 10)
                        Column {
                            Text(c.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                c.type.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

private fun Modifier.clickableText(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
