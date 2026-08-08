package org.revelar.photoviewer.ui.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.revelar.photoviewer.data.Photo
import org.revelar.photoviewer.data.PhotoMetadata
import org.revelar.photoviewer.util.aperture
import org.revelar.photoviewer.util.aspectRatio
import org.revelar.photoviewer.util.buildCaption
import org.revelar.photoviewer.util.bytes
import org.revelar.photoviewer.util.focal
import org.revelar.photoviewer.util.fullDate
import org.revelar.photoviewer.util.headlineDate
import org.revelar.photoviewer.util.hex
import org.revelar.photoviewer.util.shutter
import org.revelar.photoviewer.util.timeOfDay

@Composable
fun MetadataPanel(photo: Photo, meta: PhotoMetadata) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
    ) {
        // Cabeçalho editorial
        meta.placeName?.let {
            Eyebrow(it, color = MaterialTheme.colorScheme.primary)
        } ?: meta.camera?.let {
            Eyebrow(it, color = MaterialTheme.colorScheme.primary)
        }
        Text(
            text = headlineDate(meta.dateTakenMillis) ?: photo.displayName.substringBeforeLast('.'),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        val dek = listOfNotNull(meta.lens, timeOfDay(meta.dateTakenMillis)).joinToString(" · ")
        if (dek.isNotBlank()) {
            Text(
                text = dek,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            modifier = Modifier.padding(vertical = 20.dp)
        )

        // Legenda narrativa
        val caption = buildCaption(meta)
        if (caption.isNotBlank()) {
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 28.dp)
            )
        }

        // Paleta
        if (meta.palette.isNotEmpty()) {
            BlockTitle("Paleta")
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                meta.palette.forEach { c -> Swatch(c) }
            }
        }

        // O que a IA vê (ML Kit, no dispositivo)
        if (meta.labels.isNotEmpty()) {
            BlockTitle("O que a foto mostra")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 28.dp)
            ) {
                meta.labels.forEach { label -> Chip(label) }
            }
        }

        // Ficha técnica
        BlockTitle("Ficha técnica")
        val specs = buildList {
            meta.camera?.let { add("Câmera" to it) }
            meta.lens?.let { add("Lente" to it) }
            focal(meta.focalLengthMm)?.let { add("Distância focal" to it) }
            aperture(meta.aperture)?.let { add("Abertura" to it) }
            shutter(meta.exposureSeconds)?.let { add("Obturador" to it) }
            meta.iso?.let { add("ISO" to it.toString()) }
            fullDate(meta.dateTakenMillis)?.let { add("Data" to it) }
            add("Dimensões" to "${photo.width} × ${photo.height} px")
            add("Proporção" to aspectRatio(photo.width, photo.height))
            add("Arquivo" to "${photo.mimeType.substringAfter('/').uppercase()} · ${bytes(photo.sizeBytes)}")
        }
        specs.forEach { (k, v) -> SpecRow(k, v) }

        // Mapa
        if (meta.hasLocation) {
            Spacer(Modifier.height(28.dp))
            BlockTitle("Onde")
            OsmMapView(
                latitude = meta.latitude!!,
                longitude = meta.longitude!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Text(
                text = "%.5f, %.5f".format(meta.latitude, meta.longitude),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun Eyebrow(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@Composable
private fun BlockTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(bottom = 14.dp)
    )
}

@Composable
private fun Swatch(color: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(color))
        )
        Text(
            text = hex(color),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun Chip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
private fun SpecRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
}
