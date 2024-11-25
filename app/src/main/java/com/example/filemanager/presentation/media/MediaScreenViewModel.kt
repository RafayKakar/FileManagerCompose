package com.example.filemanager.presentation.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetMediaFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaScreenViewModel @Inject constructor(private val getMediaFiles: GetMediaFiles) : ViewModel() {

    private val _mediaFiles = MutableStateFlow<PagingData<FileItem>>(PagingData.empty())
    val mediaFiles = _mediaFiles.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO)  {
            getMediaFiles()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _mediaFiles.value = pagingData
                }
        }
    }
}