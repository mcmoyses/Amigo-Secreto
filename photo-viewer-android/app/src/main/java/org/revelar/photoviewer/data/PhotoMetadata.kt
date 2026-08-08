package org.revelar.photoviewer.data

/** Metadados ricos derivados de uma foto: EXIF, GPS, paleta e etiquetas de IA. */
data class PhotoMetadata(
    val camera: String? = null,
    val lens: String? = null,
    val focalLengthMm: Double? = null,
    val aperture: Double? = null,
    val exposureSeconds: Double? = null,
    val iso: Int? = null,
    val dateTakenMillis: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val placeName: String? = null,
    val palette: List<Int> = emptyList(),
    val dominant: Int = 0xFFC8A97E.toInt(),
    val onDominant: Int = 0xFF0E0D0C.toInt(),
    val labels: List<String> = emptyList(),
) {
    val hasLocation: Boolean get() = latitude != null && longitude != null
}
