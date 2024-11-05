package com.example.filemanager.domain.usecases.file_handling

import androidx.paging.PagingData
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDocuments @Inject constructor(private val documentsRepository: DocumentsRepository) {
    suspend operator fun invoke(): Flow<List<FileItem>> {
        return documentsRepository.getDocuments()
    }
}