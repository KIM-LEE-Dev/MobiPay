package com.kimnlee.cardmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.repository.CardManagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardManagementViewModel : ViewModel() {
    private val repository = CardManagementRepository()
    private val _photoUiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val photoUiState: StateFlow<PhotoUiState> = _photoUiState

    init {
        fetchPhotos()
    }
    fun fetchPhotos() {
        viewModelScope.launch {
            _photoUiState.value = PhotoUiState.Loading
            try {
                val photos = repository.getPhotos()
                _photoUiState.value = PhotoUiState.Success(photos)
            } catch (e: Exception) {
                _photoUiState.value = PhotoUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }
}

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photos>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}
