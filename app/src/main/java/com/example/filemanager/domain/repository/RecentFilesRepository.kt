package com.example.filemanager.domain.repository

import com.example.filemanager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RecentFilesRepository {
    suspend fun getRecentFiles(): Flow<List<FileItem>>
}