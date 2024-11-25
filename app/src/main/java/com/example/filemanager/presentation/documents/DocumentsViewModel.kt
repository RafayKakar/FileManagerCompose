package com.example.filemanager.presentation.documents

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetDocuments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DocumentsViewModel @Inject constructor(private val getDocumentsUseCase: GetDocuments) :
    ViewModel() {

    private val _documents = MutableStateFlow<PagingData<FileItem>>(PagingData.empty())
    val documents = _documents.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO)  {
            getDocumentsUseCase()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _documents.value = pagingData
                }
        }
    }
}