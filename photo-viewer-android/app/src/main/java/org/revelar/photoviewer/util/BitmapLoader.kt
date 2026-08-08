package org.revelar.photoviewer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import kotlin.math.ceil

/** Decodifica um bitmap reduzido e legível (software) para paleta e ML. */
object BitmapLoader {
    fun load(context: Context, uri: Uri, maxDim: Int = 1200): Bitmap? {
        return try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                // Software allocator para que os pixels possam ser lidos (Palette / ML Kit).
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = false
                val largest = maxOf(info.size.width, info.size.height)
                if (largest > maxDim) {
                    decoder.setTargetSampleSize(ceil(largest.toDouble() / maxDim).toInt())
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
