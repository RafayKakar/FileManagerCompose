package com.example.filemanager.domain.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface OtherFilesRepository {
    suspend fun getOtherFiles(): Flow<File>
}