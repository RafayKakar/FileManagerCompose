package com.example.filemanager.domain.repository

import com.example.filemanager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaFilesRepository {
    suspend fun getMediaFiles(page: Int, pageSize: Int): Flow<List<FileItem>>
}