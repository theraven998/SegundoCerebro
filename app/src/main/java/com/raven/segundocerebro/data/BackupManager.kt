package com.raven.segundocerebro.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object BackupManager {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun export(context: Context, uri: Uri, repo: Repository): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                val data = repo.exportSnapshot()
                context.contentResolver.openOutputStream(uri)!!.use { out ->
                    out.write(json.encodeToString(BackupData.serializer(), data).toByteArray())
                }
                data.notes.size
            }
        }

    suspend fun import(context: Context, uri: Uri, repo: Repository): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                val text = context.contentResolver.openInputStream(uri)!!
                    .use { it.readBytes().decodeToString() }
                val data = json.decodeFromString(BackupData.serializer(), text)
                repo.importSnapshot(data)
                data.notes.size
            }
        }
}
