package com.example.filemanager.domain.usecases.file_handling

import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.RecentFilesRepository
import javax.inject.Inject

class GetRecentFiles @Inject constructor(val recentFilesRepository: RecentFilesRepository) {

    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<List<FileItem>> {
       return recentFilesRepository.getRecentFiles()
    }

}