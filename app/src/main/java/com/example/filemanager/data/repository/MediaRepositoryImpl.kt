package com.example.filemanager.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.webkit.MimeTypeMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.filemanager.data.remote.FilesPagingSource
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.MediaFilesRepository
import com.example.filemanager.utils.getFormattedTime
import com.example.filemanager.utils.isFileEmptyHiddenOrCache
import com.example.filemanager.utils.sizeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : MediaFilesRepository {

    var mediaFilesList = ArrayList<FileItem>()


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

    override suspend fun getMediaFiles(): Flow<PagingData<FileItem>> = flow {

        val table = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val where = (MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=?")

        context.contentResolver.query(
            table,
            columns,
            where,
            mimeTypes,
            null
        )?.let { fileCursor ->

            val nameIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            var channel = Channel<FileItem>(Channel.UNLIMITED)
            CoroutineScope(Dispatchers.IO).launch {
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
                        channel.send(fileItem)
                }

                channel.close() // Close the channel when all items are processed
            }
            fileCursor.close()


            emitAll(
                Pager(
                    config = PagingConfig(pageSize = 10),
                    pagingSourceFactory = {
                        FilesPagingSource(channel.receiveAsFlow())
                    }
                ).flow
            )
        }
    }

}
