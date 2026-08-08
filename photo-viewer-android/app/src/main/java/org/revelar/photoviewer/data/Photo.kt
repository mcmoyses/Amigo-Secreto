package org.revelar.photoviewer.data

import android.net.Uri

/** Uma foto da galeria do aparelho, com o básico vindo do MediaStore. */
data class Photo(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val mimeType: String,
)
