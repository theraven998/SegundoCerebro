package com.raven.segundocerebro.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.raven.segundocerebro.ui.components.Dot
import com.raven.segundocerebro.ui.components.EmptyState
import com.raven.segundocerebro.ui.theme.accent
import com.raven.segundocerebro.ui.vm.ParaListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParaListScreen(
    onBack: () -> Unit,
    onOpenContainer: (String) -> Unit,
    vm: ParaListViewModel = viewModel()
) {
    val containers by vm.containers.collectAsStateWithLifecycle()
    var creating by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(vm.type.label, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            vm.type.tagline,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                onClick = { creating = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Rounded.Add, contentDescription = "Nuevo") }
        }
    ) { pad ->
        if (containers.isEmpty()) {
            Column(Modifier.padding(pad)) {
                EmptyState("Sin ${vm.type.label.lowercase()}", "Crea el primero con el botón +.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(pad).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(containers, key = { it.id }) { c ->
                    Card(
                        onClick = { onOpenContainer(c.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Dot(c.type.accent(), 10)
                            Column(Modifier.weight(1f)) {
                                Text(c.name, style = MaterialTheme.typography.titleMedium)
                                if (c.description.isNotBlank()) {
                                    Text(
                                        c.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (creating) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { creating = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Nuevo en ${vm.type.label}", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nombre") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc, onValueChange = { desc = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.create(name, desc); creating = false },
                    enabled = name.isNotBlank()
                ) { Text("Crear") }
            },
            dismissButton = { TextButton(onClick = { creating = false }) { Text("Cancelar") } }
        )
    }
}
