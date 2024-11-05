package com.example.filemanager.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.webkit.MimeTypeMap
import com.example.filemanager.domain.model.FileItem
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


class OthersRepositoryImpl @Inject constructor(
    @ApplicationContext
    val context: Context
) : RecentFilesRepository {

    var otherFiles = ArrayList<FileItem>()

    override suspend fun getRecentFiles(): Flow<List<FileItem>> {

        val rar = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rar")
        val zip = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
        val apk = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")


        val sortOrder = MediaStore.Audio.Media.DATE_ADDED

        val table = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val column = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )

        val where = (MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=?")

        val args = arrayOf(
            rar,
            zip,
            apk,
        )

        context.contentResolver.query(
            table,
            column,
            where,
            args,
            sortOrder
        )?.let { fileCursor ->

            val nameIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            while (fileCursor.moveToNext()) {

                var nameCR = fileCursor.getString(nameIndex)
                var pathCR = fileCursor.getString(dataIndex)
                var typeCR = fileCursor.getString(typeIndex)
                val sizeCR = context.sizeFormatter(fileCursor.getString(sizeIndex).toLong())
                val dateCR = getFormattedTime(fileCursor.getString(dateIndex).toLong())

                if (nameCR == null) nameCR =
                    fileCursor.getString(fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
                if (pathCR == null) pathCR = ""
                if (typeCR == null) typeCR = ""

                var fileItem = FileItem(
                    nameCR,
                    pathCR,
                    typeCR,
                    sizeCR,
                    dateCR,
                    fileCursor.getString(dateIndex).toLong()
                )

                val (isEmpty, isHidden, isCache) = isFileEmptyHiddenOrCache(
                    File(fileItem.path),
                    context
                )

                if (!isEmpty && !isHidden && !isCache)
                    otherFiles.add(fileItem)
            }

            fileCursor.close()
        }


        return flow {
            emit(
                otherFiles
            )
        }.flowOn(Dispatchers.IO)
    }
}
