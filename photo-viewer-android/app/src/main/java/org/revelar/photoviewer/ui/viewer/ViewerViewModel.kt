package org.revelar.photoviewer.ui.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.revelar.photoviewer.data.MediaRepository
import org.revelar.photoviewer.data.MetadataLoader
import org.revelar.photoviewer.data.Photo
import org.revelar.photoviewer.data.PhotoMetadata

sealed interface ViewerUiState {
    data object Loading : ViewerUiState
    data class Ready(val photo: Photo, val meta: PhotoMetadata) : ViewerUiState
    data object Error : ViewerUiState
}

class ViewerViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MediaRepository(app)
    private val metadataLoader = MetadataLoader(app)

    private val _uiState = MutableStateFlow<ViewerUiState>(ViewerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var loadedId: Long? = null

    fun load(photoId: Long) {
        if (loadedId == photoId) return
        loadedId = photoId
        viewModelScope.launch {
            _uiState.value = ViewerUiState.Loading
            val photo = repository.photoById(photoId)
            if (photo == null) {
                _uiState.value = ViewerUiState.Error
                return@launch
            }
            // Mostra a foto imediatamente com metadados vazios, depois enriquece.
            _uiState.value = ViewerUiState.Ready(photo, PhotoMetadata())
            val meta = metadataLoader.load(photo)
            _uiState.value = ViewerUiState.Ready(photo, meta)
        }
    }
}
