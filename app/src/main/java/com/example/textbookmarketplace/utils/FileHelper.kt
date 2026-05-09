package com.example.textbookmarketplace.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun saveImage(uri: Uri): String? {
        return copyToPrivate(uri, "images", ".jpg")
    }

    fun saveDocument(uri: Uri): String? {
        val ext = getExtension(uri)
        return copyToPrivate(uri, "documents", ext)
    }

    fun getFileTypeFromPath(path: String?): String? {
        if (path == null) return null
        return when {
            path.endsWith(".pdf") -> "pdf"
            path.endsWith(".docx") -> "docx"
            else -> null
        }
    }

    private fun copyToPrivate(uri: Uri, folder: String, ext: String): String? {
        return try {
            val dir = File(context.filesDir, folder).apply { mkdirs() }
            val name = UUID.randomUUID().toString() + ext
            val outFile = File(dir, name)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getExtension(uri: Uri): String {
        val type = context.contentResolver.getType(uri)
        return when {
            type?.contains("pdf") == true -> ".pdf"
            type?.contains("word") == true || type?.contains("document") == true -> ".docx"
            else -> ".bin"
        }
    }
}