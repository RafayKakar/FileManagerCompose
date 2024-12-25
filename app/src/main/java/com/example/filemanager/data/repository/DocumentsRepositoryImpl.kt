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
import com.example.filemanager.domain.repository.DocumentsRepository
import com.example.filemanager.utils.getFormattedTime
import com.example.filemanager.utils.isFileEmptyHiddenOrCache
import com.example.filemanager.utils.sizeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DocumentsRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : DocumentsRepository {

    // Predefined MIME types for document files
    private val mimeTypes = listOf(
        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt"
    ).mapNotNull { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }

    // Columns to retrieve
    private val columns = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED
    )

    override suspend fun getDocuments(): Flow<PagingData<FileItem>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { FilesPagingSource(fetchDocumentFiles()) }
        ).flow
    }

    private fun fetchDocumentFiles(): Flow<FileItem> = flow {
        val table = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        // Build query conditions dynamically
        val selection = mimeTypes.joinToString(" OR ") { "${MediaStore.Files.FileColumns.MIME_TYPE}=?" }
        val selectionArgs = mimeTypes.toTypedArray()

        context.contentResolver.query(
            table,
            columns,
            selection,
            selectionArgs,
            "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC" // Order by most recent
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                    ?: cursor.getString(titleIndex) ?: ""
                val path = cursor.getString(dataIndex) ?: ""
                val type = cursor.getString(typeIndex) ?: ""
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
                                type = type,
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
