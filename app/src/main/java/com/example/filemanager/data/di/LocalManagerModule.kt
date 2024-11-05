package com.example.filemanager.data.di

import com.example.filemanager.data.manager.LocalUserMangerImpl
import com.example.filemanager.domain.manager.LocalUserManger
import com.google.gson.annotations.Since
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class LocalManagerModule {

    @Binds
    @Singleton
    abstract fun bindLocalUserManager(localUserMangerImpl: LocalUserMangerImpl):LocalUserManger

}