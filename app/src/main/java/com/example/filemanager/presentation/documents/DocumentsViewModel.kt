package com.example.filemanager.presentation.documents

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.usecases.file_handling.GetDocuments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DocumentsViewModel @Inject constructor(val getDocumentsUseCase: GetDocuments) : ViewModel() {

    private val _documents = MutableStateFlow<List<FileItem>>(emptyList())
    val documents = _documents.asStateFlow()

    init {
        fetchDocuments()
    }

    private fun fetchDocuments() {
        viewModelScope.launch {
            getDocumentsUseCase.invoke().collect { file ->
                _documents.value = file
            }
        }
    }
}