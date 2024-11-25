package com.example.filemanager.domain.usecases.file_handling

import androidx.paging.PagingData
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.RecentFilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentFiles @Inject constructor(val recentFilesRepository: RecentFilesRepository) {

     operator suspend fun invoke(): Flow<PagingData<FileItem>> {
       return recentFilesRepository.getRecentFiles()
    }

}