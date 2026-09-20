package com.scrollbudget.app

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Books bundled inside the app (in assets/books/) that the user can open
 * with their phone's default PDF reader via a "Read Book" button.
 */
object BookOpener {

    data class Book(val assetFileName: String, val displayName: String)

    val books = listOf(
        Book("looksmaxxing_bible.pdf", "The Looksmaxxing Bible (2026)"),
        Book("subconscious_mind.pdf", "F*ck Your Subconscious Mind")
    )

    fun open(context: Context, book: Book) {
        try {
            val booksDir = File(context.cacheDir, "books")
            if (!booksDir.exists()) booksDir.mkdirs()

            val outFile = File(booksDir, book.assetFileName)

            // Copy from assets only if not already copied (avoids re-copying a multi-MB file every open)
            if (!outFile.exists()) {
                context.assets.open("books/${book.assetFileName}").use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    "No PDF reader app is installed on this phone",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Couldn't open the book: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
