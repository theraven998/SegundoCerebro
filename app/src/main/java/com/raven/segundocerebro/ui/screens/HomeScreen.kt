package com.raven.segundocerebro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.segundocerebro.data.ParaType
import com.raven.segundocerebro.ui.components.Dot
import com.raven.segundocerebro.ui.components.EmptyState
import com.raven.segundocerebro.ui.components.NoteCard
import com.raven.segundocerebro.ui.components.SectionLabel
import com.raven.segundocerebro.ui.theme.accent
import com.raven.segundocerebro.ui.vm.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenInbox: () -> Unit,
    onOpenPara: (ParaType) -> Unit,
    onOpenNote: (String) -> Unit,
    onNewNote: () -> Unit,
    onSearch: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val inbox by vm.inboxCount.collectAsStateWithLifecycle()
    val counts by vm.counts.collectAsStateWithLifecycle()
    val recent by vm.recent.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var menu by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { vm.exportTo(it) { msg -> scope.launch { snackbar.showSnackbar(msg) } } } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { vm.importFrom(it) { msg -> scope.launch { snackbar.showSnackbar(msg) } } } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Segundo Cerebro", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Captura, organiza, destila",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Outlined.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { menu = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Más")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(
                            text = { Text("Exportar backup (JSON)") },
                            onClick = { menu = false; exportLauncher.launch("segundo-cerebro-backup.json") }
                        )
                        DropdownMenuItem(
                            text = { Text("Importar backup") },
                            onClick = { menu = false; importLauncher.launch(arrayOf("application/json")) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewNote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Text("  Capturar")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                InboxBanner(count = inbox, onClick = onOpenInbox)
            }
            item { SectionLabel("Tu P.A.R.A.", Modifier.padding(top = 8.dp)) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ParaType.organizable.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { type ->
                                Box(Modifier.weight(1f)) {
                                    ParaCard(type, counts[type] ?: 0) { onOpenPara(type) }
                                }
                            }
                            if (row.size == 1) Box(Modifier.weight(1f))
                        }
                    }
                }
            }
            item { SectionLabel("Reciente", Modifier.padding(top = 8.dp)) }
            if (recent.isEmpty()) {
                item {
                    EmptyState(
                        "Aún no hay notas",
                        "Pulsa Capturar para guardar tu primera idea."
                    )
                }
            } else {
                items(recent, key = { it.id }) { note ->
                    NoteCard(note, showType = true) { onOpenNote(note.id) }
                }
            }
        }
    }
}

@Composable
private fun InboxBanner(count: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Bandeja de entrada", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (count == 0) "Todo organizado" else "$count por organizar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "$count",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ParaCard(type: ParaType, count: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().height(118.dp)
    ) {
        Column(
            Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Dot(type.accent(), 10)
                Text("$count", style = MaterialTheme.typography.titleMedium)
            }
            Column {
                Text(type.label, style = MaterialTheme.typography.headlineSmall)
                Text(
                    type.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
