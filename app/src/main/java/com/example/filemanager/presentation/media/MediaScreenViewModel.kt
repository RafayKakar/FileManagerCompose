package com.example.filemanager.presentation.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetMediaFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaScreenViewModel @Inject constructor(var getMediaFiles: GetMediaFiles) : ViewModel() {

    private var currentPage = 0
    private val itemsPerPage = 20

    // Holds the list of media files (exposed as a StateFlow)
    private val _mediaFiles = MutableStateFlow<List<FileItem>>(emptyList())
    var mediafiles = _mediaFiles.asStateFlow()

    // Loading state to prevent duplicate loads
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadNextPage() // Load the first page on initialization
    }

    // Function to load the next page of media files
    fun loadNextPage() {

        if (_isLoading.value) return // Avoid duplicate calls

        viewModelScope.launch {
            _isLoading.value = true
            val newFiles = getMediaFiles.invoke(page = currentPage, pageSize = itemsPerPage).first()
            _mediaFiles.value = _mediaFiles.value + newFiles
            currentPage++ // Increment page number for next load
            _isLoading.value = false
        }

    }

}