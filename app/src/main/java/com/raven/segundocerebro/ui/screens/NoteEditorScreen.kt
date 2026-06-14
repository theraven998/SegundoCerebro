package com.raven.segundocerebro.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.raven.segundocerebro.data.ImageStore
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.segundocerebro.data.Note
import com.raven.segundocerebro.data.ParaType
import com.raven.segundocerebro.ui.components.Dot
import com.raven.segundocerebro.ui.theme.accent
import com.raven.segundocerebro.ui.vm.EditorViewModel
import java.util.UUID

private val distillLabels = listOf("Capturado", "Resaltado", "Resumido", "Esencial")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    onBack: () -> Unit,
    vm: EditorViewModel = viewModel()
) {
    val existing by vm.note.collectAsStateWithLifecycle()
    val containers by vm.containers.collectAsStateWithLifecycle()

    val newId = rememberSaveable { UUID.randomUUID().toString() }
    var seeded by rememberSaveable { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var summary by rememberSaveable { mutableStateOf("") }
    var tagsText by rememberSaveable { mutableStateOf("") }
    var favorite by rememberSaveable { mutableStateOf(false) }
    var distill by rememberSaveable { mutableStateOf(0) }
    var containerId by rememberSaveable { mutableStateOf(vm.initialContainerId) }
    var createdAt by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    // Nombres de imagen unidos por '|' (no aparece en nombres uuid).
    var imagesCsv by rememberSaveable { mutableStateOf("") }
    val imageNames = if (imagesCsv.isBlank()) emptyList() else imagesCsv.split("|")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var viewer by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) scope.launch {
            ImageStore.import(context, uri)?.let { name ->
                imagesCsv = (imageNames + name).joinToString("|")
            }
        }
    }

    // Sembrar campos una vez con la nota existente.
    LaunchedEffect(existing, vm.isNew) {
        if (!seeded) {
            val n = existing
            if (vm.isNew) {
                seeded = true
            } else if (n != null) {
                title = n.title; content = n.content; summary = n.summary
                tagsText = n.tags.joinToString(", "); favorite = n.isFavorite
                distill = n.distillLevel; containerId = n.containerId; createdAt = n.createdAt
                imagesCsv = n.images.joinToString("|")
                seeded = true
            }
        }
    }

    val id = if (vm.isNew) newId else (existing?.id ?: newId)
    val selectedType = containers.firstOrNull { it.id == containerId }?.type ?: ParaType.INBOX

    fun build(): Note = Note(
        id = id,
        title = title.trim(),
        content = content.trim(),
        summary = summary.trim(),
        tags = tagsText.split(",").map { it.trim().removePrefix("#") }.filter { it.isNotBlank() },
        images = imageNames,
        containerId = containerId,
        type = selectedType,
        isFavorite = favorite,
        distillLevel = distill,
        createdAt = createdAt
    )

    fun persist() {
        val draft = build()
        if (draft.title.isNotBlank() || draft.content.isNotBlank() ||
            draft.summary.isNotBlank() || draft.images.isNotEmpty()
        ) {
            vm.save(draft)
        }
    }

    var picking by remember { mutableStateOf(false) }
    val sheet = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { persist(); onBack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Guardar y volver")
                    }
                },
                actions = {
                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(
                            if (favorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!vm.isNew) {
                        IconButton(onClick = { existing?.let { vm.delete(it) }; onBack() }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Eliminar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { pad ->
        val plain = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
        Column(
            Modifier.padding(pad).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ) {
            // Organizar: contenedor destino
            Row(
                Modifier.fillMaxWidth().clickable { picking = true }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Dot(selectedType.accent(), 10)
                Text(
                    containers.firstOrNull { it.id == containerId }?.name ?: "Bandeja · sin organizar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("· cambiar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Título", style = MaterialTheme.typography.headlineMedium) },
                textStyle = MaterialTheme.typography.headlineMedium,
                colors = plain, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                placeholder = { Text("Escribe lo que capturas…", style = MaterialTheme.typography.bodyLarge) },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = plain, modifier = Modifier.fillMaxWidth()
            )

            // Adjuntos de imagen
            Row(
                Modifier.fillMaxWidth().clickable {
                    pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.AddPhotoAlternate, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Añadir imagen", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (imageNames.isNotEmpty()) {
                LazyRow(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(imageNames, key = { it }) { name ->
                        Box {
                            AsyncImage(
                                model = ImageStore.file(context, name),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp))
                                    .clickable { viewer = name }
                            )
                            Box(
                                Modifier.padding(4.dp).size(22.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f))
                                    .clickable {
                                        imagesCsv = (imageNames - name).joinToString("|")
                                        ImageStore.delete(context, name)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Close, contentDescription = "Quitar",
                                    tint = Color.White, modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)

            Text("DESTILAR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                distillLabels.forEachIndexed { i, label ->
                    FilterChip(
                        selected = distill == i,
                        onClick = { distill = i },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            OutlinedTextField(
                value = summary, onValueChange = { summary = it },
                label = { Text("Idea clave / resumen") },
                placeholder = { Text("La esencia en una frase") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = tagsText, onValueChange = { tagsText = it },
                label = { Text("Etiquetas") },
                placeholder = { Text("idea, libro, salud") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 40.dp)
            )
        }
    }

    viewer?.let { name ->
        Dialog(onDismissRequest = { viewer = null }) {
            AsyncImage(
                model = ImageStore.file(context, name),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { viewer = null }
            )
        }
    }

    if (picking) {
        ModalBottomSheet(
            onDismissRequest = { picking = false },
            sheetState = sheet,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(20.dp, 0.dp, 20.dp, 32.dp)) {
                Text("Organizar en…", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    Modifier.fillMaxWidth().clickable { containerId = null; picking = false }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Dot(ParaType.INBOX.accent(), 10)
                    Text("Bandeja · sin organizar", style = MaterialTheme.typography.titleMedium)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                containers.forEach { c ->
                    Row(
                        Modifier.fillMaxWidth().clickable { containerId = c.id; picking = false }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Dot(c.type.accent(), 10)
                        Column {
                            Text(c.name, style = MaterialTheme.typography.titleMedium)
                            Text(c.type.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
