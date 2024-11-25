package com.example.filemanager.presentation.recents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetRecentFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentsViewModel @Inject constructor(private val getRecentFiles: GetRecentFiles) : ViewModel() {

    private val _recentFiles = MutableStateFlow<PagingData<FileItem>>(PagingData.empty())
    val recentFiles = _recentFiles.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentFiles()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _recentFiles.value = pagingData
                }
        }
    }

}