package com.raven.segundocerebro.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

/** Contenedor P.A.R.A.: un proyecto, área, recurso o carpeta de archivo. */
@Serializable
@Entity(tableName = "containers")
data class Container(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ParaType,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Nota. containerId nulo = vive en la Bandeja (Inbox).
 * distillLevel 0..3 modela el paso "Distill" (CODE): cuánto se ha refinado.
 */
@Serializable
@Entity(
    tableName = "notes",
    indices = [Index("containerId"), Index("type")]
)
data class Note(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val summary: String = "",
    val tags: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val containerId: String? = null,
    val type: ParaType = ParaType.INBOX,
    val source: String = "",
    val isFavorite: Boolean = false,
    val distillLevel: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/** Nota junto al nombre de su contenedor (consulta de solo lectura). */
data class NoteWithContainer(
    val note: Note,
    val containerName: String?
)
