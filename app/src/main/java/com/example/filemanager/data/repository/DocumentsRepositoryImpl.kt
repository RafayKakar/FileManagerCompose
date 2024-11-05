package com.example.filemanager.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.domain.repository.DocumentsRepository
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


class DocumentsRepositoryImpl @Inject constructor(
    @ApplicationContext
    val context: Context
) : DocumentsRepository {

    var documentfileList = ArrayList<FileItem>()

    override suspend fun getDocuments(): Flow<List<FileItem>> {

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
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=?")
        val args = arrayOf(pdf, doc, docx, ppt, pptx, excel, excelX,txt)

        context.contentResolver.query(
            table,
            column,
            where,
            args,
            null
        )?.let { fileCursor ->

            val nameIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateIndex = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

            while (fileCursor.moveToNext()) {

                var nameCR = fileCursor.getString(nameIndex)
                var pathCR = fileCursor.getString(dataIndex)
                var typeCR = fileCursor.getString(typeIndex)
                val sizeCR = context.sizeFormatter(fileCursor.getString(sizeIndex).toLong())
                val dateCR = getFormattedTime(fileCursor.getString(dateIndex).toLong())

                if (nameCR == null) nameCR = fileCursor.getString(fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
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
                    documentfileList.add(fileItem)
            }

            fileCursor.close()
        }


        return flow {
            emit(
                documentfileList
            )
        }.flowOn(Dispatchers.IO)
    }
}
