package com.example.filemanager.data.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.filemanager.data.remote.FilesPagingSource
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.DocumentsRepository
import com.example.filemanager.utils.getFormattedTime
import com.example.filemanager.utils.isFileEmptyHiddenOrCache
import com.example.filemanager.utils.sizeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


class DocumentsRepositoryImpl @Inject constructor(
    @ApplicationContext
    val context: Context
) : DocumentsRepository {

    init {
        Log.d("initsrepository", "Initialise repo:docs ")
        GlobalScope.launch(Dispatchers.IO) {
            getDocuments()
        }
    }

    override suspend fun getDocuments(): Flow<PagingData<FileItem>> = flow {

        val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc")
        val docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx")
        val ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt")
        val pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx")
        val excel = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls")
        val excelX = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx")
        val txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt")

        val table = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val columns = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )

        val where = (MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=?")
        val args = arrayOf(pdf, doc, docx, ppt, pptx, excel, excelX, txt)

        context.contentResolver.query(table, columns, where, args, null)?.use { fileCursor ->
            val nameIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex =
                fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            var channel = Channel<FileItem>(Channel.UNLIMITED)

            // Coroutine to emit items as they are fetched
            CoroutineScope(Dispatchers.IO).launch {

                while (fileCursor.moveToNext()) {
                    var name = fileCursor.getString(nameIndex)
                    var path = fileCursor.getString(dataIndex) ?: ""
                    var type = fileCursor.getString(typeIndex) ?: ""
                    val size = context.sizeFormatter(fileCursor.getString(sizeIndex).toLong())
                    val date = getFormattedTime(fileCursor.getString(dateIndex).toLong())

                    if (name == null) {
                        name =
                            fileCursor.getString(fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
                    }

                    var fileItem = FileItem(
                        name,
                        path,
                        type,
                        size,
                        date,
                        fileCursor.getString(dateIndex).toLong()
                    )

                    val (isEmpty, isHidden, isCache) = isFileEmptyHiddenOrCache(
                        File(fileItem.path),
                        context
                    )

                    if (!isEmpty && !isHidden && !isCache) {
                        channel.send(fileItem)
                    }
                    channel.close() // Close the channel when all items are processed

                }


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


}
