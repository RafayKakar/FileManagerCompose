package com.example.filemanager.domain.usecases.file_handling

import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.MediaFilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaFiles @Inject constructor(private val mediaFilesRepository: MediaFilesRepository) {

    suspend operator fun invoke(page: Int, pageSize: Int): Flow<List<FileItem>> {
        return mediaFilesRepository.getMediaFiles(page, pageSize)
    }

}
