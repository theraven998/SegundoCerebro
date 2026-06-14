package com.raven.segundocerebro.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Imágenes locales bajo filesDir/images. En la nota se guarda solo el nombre. */
object ImageStore {
    private fun dir(context: Context): File =
        File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

    fun file(context: Context, name: String): File = File(dir(context), name)

    /** Copia una imagen de la galería al almacenamiento interno. Devuelve el nombre. */
    suspend fun import(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val name = "img_${UUID.randomUUID()}.jpg"
            val out = File(dir(context), name)
            context.contentResolver.openInputStream(uri)!!.use { input ->
                out.outputStream().use { input.copyTo(it) }
            }
            name
        }.getOrNull()
    }

    fun delete(context: Context, name: String) {
        runCatching { file(context, name).delete() }
    }
}
