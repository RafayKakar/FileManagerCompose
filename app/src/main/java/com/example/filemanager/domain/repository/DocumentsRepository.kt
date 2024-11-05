package com.example.filemanager.domain.repository

import com.example.filemanager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DocumentsRepository {
    suspend fun getDocuments(): Flow<List<FileItem>>
}