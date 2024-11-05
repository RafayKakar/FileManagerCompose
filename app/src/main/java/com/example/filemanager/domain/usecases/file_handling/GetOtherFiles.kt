package com.example.filemanager.domain.usecases.file_handling

import com.example.filemanager.domain.repository.OtherFilesRepository
import javax.inject.Inject

class GetOtherFiles @Inject constructor(private val otherFilesRepository: OtherFilesRepository) {

    suspend operator fun invoke() {
        otherFilesRepository.getOtherFiles()
    }

}