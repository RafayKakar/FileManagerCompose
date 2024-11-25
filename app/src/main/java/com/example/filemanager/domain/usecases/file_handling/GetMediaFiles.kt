package com.example.filemanager.domain.usecases.file_handling

import androidx.paging.PagingData
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.MediaFilesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaFiles @Inject constructor(private val mediaFilesRepository: MediaFilesRepository) {

     operator suspend fun invoke(): Flow<PagingData<FileItem>> {
        return mediaFilesRepository.getMediaFiles()
    }

}
