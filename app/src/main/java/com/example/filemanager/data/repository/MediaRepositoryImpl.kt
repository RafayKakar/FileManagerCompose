package com.example.filemanager.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.MediaFilesRepository
import com.example.filemanager.domain.repository.RecentFilesRepository
import com.example.filemanager.utils.getFormattedTime
import com.example.filemanager.utils.isFileEmptyHiddenOrCache
import com.example.filemanager.utils.sizeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : MediaFilesRepository {

    // Precompute constants for MIME types and columns
    private val mimeTypes = arrayOf(
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3"),
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp4"),
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"),
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"),
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("wav")
    )

    private val columns = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED
    )

    private val whereClause = mimeTypes.joinToString(" OR ") {
        "${MediaStore.Files.FileColumns.MIME_TYPE}=?"
    }

    override suspend fun getMediaFiles(page: Int, pageSize: Int): Flow<List<FileItem>> {
        val table = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

        return flow {
            val recentFilesList = mutableListOf<FileItem>()

            context.contentResolver.query(
                table, columns, whereClause, mimeTypes, sortOrder
            )?.use { cursor ->
                val startPosition = page * pageSize

                // Move to the starting position based on pagination
                if (cursor.moveToPosition(startPosition)) {
                    val nameIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val typeIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                    val dateIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

                    var count = 0

                    do {
                        // Avoid null values in key fields
                        val nameCR = cursor.getString(nameIndex)
                            ?: cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
                            ?: ""
                        val pathCR = cursor.getString(dataIndex) ?: ""
                        val typeCR = cursor.getString(typeIndex) ?: ""
                        val sizeCR = context.sizeFormatter(cursor.getLong(sizeIndex))
                        val dateCR = getFormattedTime(cursor.getLong(dateIndex))

                        val fileItem = FileItem(
                            nameCR,
                            pathCR,
                            typeCR,
                            sizeCR,
                            dateCR,
                            cursor.getLong(dateIndex)
                        )

                        // Validate and add file only if it passes validation
                        val (isEmpty, isHidden, isCache) = isFileEmptyHiddenOrCache(
                            File(fileItem.path),
                            context
                        )
                        if (!isEmpty && !isHidden && !isCache) {
                            recentFilesList.add(fileItem)
                        }

                        count++

                    } while (cursor.moveToNext() && count < pageSize)
                }
            }

            // Emit the list of paginated files
            emit(recentFilesList)


        }.flowOn(Dispatchers.IO)
    }
}
