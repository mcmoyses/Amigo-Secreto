package org.revelar.photoviewer.util

import org.revelar.photoviewer.data.PhotoMetadata
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val MESES = arrayOf(
    "janeiro", "fevereiro", "março", "abril", "maio", "junho",
    "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
)

fun headlineDate(millis: Long?): String? {
    millis ?: return null
    val d = calendar(millis)
    return "${d.day} de ${MESES[d.month]}, ${d.year}"
}

fun fullDate(millis: Long?): String? {
    millis ?: return null
    val d = calendar(millis)
    return "%02d/%02d/%04d".format(d.day, d.month + 1, d.year)
}

fun timeOfDay(millis: Long?): String? {
    millis ?: return null
    val d = calendar(millis)
    return "%02dh%02d".format(d.hour, d.minute)
}

fun shutter(seconds: Double?): String? {
    seconds ?: return null
    return if (seconds >= 1) "${seconds.roundToInt()}s" else "1/${(1 / seconds).roundToInt()}s"
}

fun aperture(f: Double?): String? = f?.let { "f/${trimZeros(it)}" }

fun focal(mm: Double?): String? = mm?.let { "${it.roundToInt()} mm" }

fun bytes(n: Long): String = when {
    n < 1024 -> "$n B"
    n < 1024 * 1024 -> "${(n / 1024.0).roundToInt()} KB"
    else -> "%.1f MB".format(n / 1024.0 / 1024.0)
}

fun aspectRatio(w: Int, h: Int): String {
    if (w <= 0 || h <= 0) return "—"
    val g = gcd(w, h)
    return "${w / g}:${h / g}"
}

fun hex(color: Int): String = "#%06X".format(0xFFFFFF and color)

/** Legenda narrativa gerada a partir dos metadados. */
fun buildCaption(meta: PhotoMetadata): String {
    val parts = mutableListOf<String>()
    meta.dateTakenMillis?.let { millis ->
        val h = calendar(millis).hour
        val periodo = when {
            h < 6 -> "na madrugada"
            h < 12 -> "de manhã"
            h < 18 -> "à tarde"
            else -> "à noite"
        }
        parts += "Capturada $periodo"
    }
    meta.camera?.let { parts += "com $it" }
    if (meta.aperture != null && meta.iso != null) {
        val luz = when {
            meta.iso >= 1600 -> ", em pouca luz"
            meta.iso <= 200 -> ", sob boa luz"
            else -> ""
        }
        parts += "a f/${trimZeros(meta.aperture)}, ISO ${meta.iso}$luz"
    }
    if (parts.isEmpty()) return ""
    return parts.joinToString(" ") + "."
}

private fun trimZeros(v: Double): String {
    val s = "%.1f".format(Locale.US, v)
    return if (s.endsWith(".0")) s.dropLast(2) else s
}

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

private data class Cal(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int)

private fun calendar(millis: Long): Cal {
    val c = java.util.Calendar.getInstance()
    c.time = Date(millis)
    return Cal(
        year = c.get(java.util.Calendar.YEAR),
        month = c.get(java.util.Calendar.MONTH),
        day = c.get(java.util.Calendar.DAY_OF_MONTH),
        hour = c.get(java.util.Calendar.HOUR_OF_DAY),
        minute = c.get(java.util.Calendar.MINUTE),
    )
}
