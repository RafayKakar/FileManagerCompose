package com.example.filemanager.presentation.recents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetRecentFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentsViewModel @Inject constructor(val getRecentFiles: GetRecentFiles) : ViewModel() {

    var _recents = MutableStateFlow<List<FileItem>>(emptyList())
    var recents = _recents.asStateFlow()

    init {
        fetchRecentFiles()
    }

    private fun fetchRecentFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentFiles.invoke().collect { file ->
                _recents.value = file
            }
        }
    }
}