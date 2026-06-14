package com.raven.segundocerebro.data

/**
 * Categorías del método P.A.R.A. de Tiago Forte.
 * INBOX = bandeja de captura sin organizar (paso Capture del flujo CODE).
 */
@kotlinx.serialization.Serializable
enum class ParaType(val label: String, val tagline: String) {
    INBOX("Bandeja", "Capturas sin organizar"),
    PROJECT("Proyectos", "Esfuerzos con fecha y meta"),
    AREA("Áreas", "Responsabilidades a mantener"),
    RESOURCE("Recursos", "Temas de interés futuro"),
    ARCHIVE("Archivo", "Inactivo, por si acaso");

    companion object {
        /** Tipos organizables (excluye la bandeja). */
        val organizable = listOf(PROJECT, AREA, RESOURCE, ARCHIVE)
    }
}
