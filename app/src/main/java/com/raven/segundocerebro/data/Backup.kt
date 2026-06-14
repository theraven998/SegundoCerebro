package com.raven.segundocerebro.data

import kotlinx.serialization.Serializable

/** Snapshot completo para exportar/importar (solo texto + nombres de imagen). */
@Serializable
data class BackupData(
    val version: Int = 2,
    val exportedAt: Long = System.currentTimeMillis(),
    val containers: List<Container> = emptyList(),
    val notes: List<Note> = emptyList()
)
