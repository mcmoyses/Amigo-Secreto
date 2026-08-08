package org.revelar.photoviewer.data

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.graphics.ColorUtils
import androidx.exifinterface.media.ExifInterface
import androidx.palette.graphics.Palette
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.revelar.photoviewer.util.BitmapLoader
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Reúne todos os metadados de uma foto: EXIF, GPS, paleta de cores e
 * etiquetas geradas por ML Kit. Tudo roda no dispositivo.
 */
class MetadataLoader(private val context: Context) {

    suspend fun load(photo: Photo): PhotoMetadata = withContext(Dispatchers.IO) {
        val exif = readExif(photo.uri)
        val bitmap = BitmapLoader.load(context, photo.uri)

        val paletteResult = bitmap?.let { extractPalette(it) } ?: PaletteResult()
        val labels = bitmap?.let { detectLabels(it) } ?: emptyList()
        val place = if (exif.latitude != null && exif.longitude != null) {
            resolvePlace(exif.latitude, exif.longitude)
        } else null

        exif.copy(
            palette = paletteResult.swatches,
            dominant = paletteResult.dominant,
            onDominant = paletteResult.onDominant,
            labels = labels,
            placeName = place,
        )
    }

    // ---------- EXIF / GPS ----------
    private fun readExif(uri: Uri): PhotoMetadata {
        val resolver = context.contentResolver
        val readUri = try {
            MediaStore.setRequireOriginal(uri)
        } catch (e: Exception) {
            uri
        }
        return try {
            resolver.openInputStream(readUri)?.use { stream ->
                val exif = ExifInterface(stream)
                val make = exif.getAttribute(ExifInterface.TAG_MAKE)?.trim()
                val model = exif.getAttribute(ExifInterface.TAG_MODEL)?.trim()
                val camera = listOfNotNull(make, model)
                    .filter { it.isNotEmpty() }
                    .joinToString(" ")
                    .ifBlank { null }

                var iso = exif.getAttributeInt(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, 0)
                if (iso == 0) iso = exif.getAttributeInt(ExifInterface.TAG_ISO_SPEED_RATINGS, 0)

                val focal = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0)
                val fnum = exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, 0.0)
                val exposure = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, 0.0)
                val latLong: DoubleArray? = exif.latLong

                PhotoMetadata(
                    camera = camera,
                    lens = exif.getAttribute(ExifInterface.TAG_LENS_MODEL)?.trim()?.ifBlank { null },
                    focalLengthMm = focal.takeIf { it > 0 },
                    aperture = fnum.takeIf { it > 0 },
                    exposureSeconds = exposure.takeIf { it > 0 },
                    iso = iso.takeIf { it > 0 },
                    dateTakenMillis = parseExifDate(exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)),
                    latitude = latLong?.getOrNull(0),
                    longitude = latLong?.getOrNull(1),
                )
            } ?: PhotoMetadata()
        } catch (e: Exception) {
            PhotoMetadata()
        }
    }

    private fun parseExifDate(raw: String?): Long? {
        if (raw.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).parse(raw)?.time
        } catch (e: Exception) {
            null
        }
    }

    // ---------- Paleta ----------
    private data class PaletteResult(
        val swatches: List<Int> = emptyList(),
        val dominant: Int = 0xFFC8A97E.toInt(),
        val onDominant: Int = 0xFF0E0D0C.toInt(),
    )

    private fun extractPalette(bitmap: Bitmap): PaletteResult {
        val palette = Palette.from(bitmap).clearFilters().maximumColorCount(24).generate()
        val dominantSwatch = palette.vibrantSwatch
            ?: palette.dominantSwatch
            ?: palette.mutedSwatch
        val dominant = dominantSwatch?.rgb ?: 0xFFC8A97E.toInt()
        val onDominant = if (ColorUtils.calculateLuminance(dominant) > 0.5) {
            0xFF0E0D0C.toInt()
        } else {
            0xFFF4EFE9.toInt()
        }
        val swatches = palette.swatches
            .sortedByDescending { it.population }
            .take(5)
            .map { it.rgb }
        return PaletteResult(swatches, dominant, onDominant)
    }

    // ---------- Etiquetas (ML Kit, no dispositivo) ----------
    private suspend fun detectLabels(bitmap: Bitmap): List<String> =
        suspendCancellableCoroutine { cont ->
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val texts = labels
                        .filter { it.confidence >= 0.6f }
                        .sortedByDescending { it.confidence }
                        .map { it.text }
                        .take(6)
                    if (cont.isActive) cont.resume(texts)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(emptyList())
                }
        }

    // ---------- Geocodificação reversa ----------
    private suspend fun resolvePlace(lat: Double, lon: Double): String? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale("pt", "BR"))
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(lat, lon, 1) { addresses ->
                        if (cont.isActive) cont.resume(addresses.firstOrNull()?.let(::formatAddress))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.let(::formatAddress)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun formatAddress(a: Address): String? {
        val city = a.locality ?: a.subAdminArea ?: a.subLocality
        return listOfNotNull(city, a.adminArea, a.countryName)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(", ")
            .ifBlank { null }
    }
}
