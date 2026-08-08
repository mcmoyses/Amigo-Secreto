package org.revelar.photoviewer.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

/** Lê as fotos da galeria via MediaStore. */
class MediaRepository(private val context: Context) {

    private val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.MIME_TYPE,
    )

    fun loadPhotos(): List<Photo> {
        val result = mutableListOf<Photo>()
        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        context.contentResolver.query(collection, projection, null, null, sort)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val wCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val hCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                result += Photo(
                    id = id,
                    uri = ContentUris.withAppendedId(collection, id),
                    displayName = c.getString(nameCol) ?: "",
                    dateAdded = c.getLong(dateCol),
                    width = c.getInt(wCol),
                    height = c.getInt(hCol),
                    sizeBytes = c.getLong(sizeCol),
                    mimeType = c.getString(mimeCol) ?: "image/*",
                )
            }
        }
        return result
    }

    fun photoById(id: Long): Photo? {
        val selection = "${MediaStore.Images.Media._ID} = ?"
        val args = arrayOf(id.toString())
        context.contentResolver.query(collection, projection, selection, args, null)?.use { c ->
            if (c.moveToFirst()) {
                val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val wCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val hCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val sizeCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                return Photo(
                    id = id,
                    uri = ContentUris.withAppendedId(collection, id),
                    displayName = c.getString(nameCol) ?: "",
                    dateAdded = c.getLong(dateCol),
                    width = c.getInt(wCol),
                    height = c.getInt(hCol),
                    sizeBytes = c.getLong(sizeCol),
                    mimeType = c.getString(mimeCol) ?: "image/*",
                )
            }
        }
        return null
    }
}
