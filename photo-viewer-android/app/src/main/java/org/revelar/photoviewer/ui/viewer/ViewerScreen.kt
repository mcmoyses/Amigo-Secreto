package org.revelar.photoviewer.ui.viewer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import org.revelar.photoviewer.data.Photo
import org.revelar.photoviewer.data.PhotoMetadata
import org.revelar.photoviewer.ui.theme.Bg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    photoId: Long,
    onBack: () -> Unit,
    viewModel: ViewerViewModel = viewModel()
) {
    LaunchedEffect(photoId) { viewModel.load(photoId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = state) {
        is ViewerUiState.Loading -> CenterBox { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        is ViewerUiState.Error -> CenterBox {
            Text("Não consegui abrir esta foto.", color = MaterialTheme.colorScheme.onBackground)
        }
        is ViewerUiState.Ready -> ViewerContent(s.photo, s.meta, onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewerContent(photo: Photo, meta: PhotoMetadata, onBack: () -> Unit) {
    var showPanel by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // A interface é tingida pela cor dominante da foto.
    val tint by animateColorAsState(targetValue = Color(meta.dominant), label = "tint")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(tint.copy(alpha = 0.38f), Bg, Bg)
                )
            )
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.displayName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 48.dp)
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        ExtendedFloatingActionButton(
            onClick = { showPanel = true },
            containerColor = tint,
            contentColor = Color(meta.onDominant),
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
            text = { Text("Informações") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }

    if (showPanel) {
        ModalBottomSheet(
            onDismissRequest = { showPanel = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MetadataPanel(photo = photo, meta = meta)
        }
    }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentAlignment = Alignment.Center
    ) { content() }
}
