package com.example.filemanager.data.di

import com.example.filemanager.data.repository.DocumentsRepositoryImpl
import com.example.filemanager.data.repository.MediaRepositoryImpl
import com.example.filemanager.data.repository.RecentsRepositoryImpl
import com.example.filemanager.domain.repository.DocumentsRepository
import com.example.filemanager.domain.repository.MediaFilesRepository
import com.example.filemanager.domain.repository.RecentFilesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDocumentRepository(
        documentsRepositoryImpl: DocumentsRepositoryImpl
    ): DocumentsRepository

    @Binds
    abstract fun bindRecentsRepository(
        recentsRepositoryImpl: RecentsRepositoryImpl
    ): RecentFilesRepository


    @Binds
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaFilesRepository

}