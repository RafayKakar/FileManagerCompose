package com.example.filemanager.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.filemanager.data.remote.FilesPagingSource
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.RecentFilesRepository
import com.example.filemanager.utils.getFormattedTime
import com.example.filemanager.utils.isFileEmptyHiddenOrCache
import com.example.filemanager.utils.sizeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class RecentsRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : RecentFilesRepository {

    override suspend fun getRecentFiles(): Flow<PagingData<FileItem>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { FilesPagingSource(fetchRecentFiles()) }
        ).flow
    }

    private fun fetchRecentFiles(): Flow<FileItem> = flow {
        val mimeTypes = listOf(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt",
            "mp3", "mp4", "jpg", "png", "jpeg", "rar", "tar", "gz", "gif",
            "svg", "zip", "apk", "wav"
        ).mapNotNull { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }

        val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
        val tableUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )

        val selection = mimeTypes.joinToString(" OR ") { "${MediaStore.Files.FileColumns.MIME_TYPE}=?" }
        val selectionArgs = mimeTypes.toTypedArray()

        context.contentResolver.query(
            tableUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor -> // Use ensures cursor is closed properly
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                    ?: cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)) ?: ""
                val path = cursor.getString(dataIndex) ?: ""
                val mimeType = cursor.getString(typeIndex) ?: ""
                val size = cursor.getLong(sizeIndex)
                val modifiedDate = cursor.getLong(dateIndex)

                val file = File(path)
                if (file.exists()) {
                    val (isEmpty, isHidden, isCache) = isFileEmptyHiddenOrCache(file, context)
                    if (!isEmpty && !isHidden && !isCache) {
                        emit(
                            FileItem(
                                name = name,
                                path = path,
                                type = mimeType,
                                size = context.sizeFormatter(size),
                                lastModifiedTime = getFormattedTime(modifiedDate),
                                lastModifiedTimeLong = modifiedDate
                            )
                        )
                    }
                }
            }
        }
    }
}

