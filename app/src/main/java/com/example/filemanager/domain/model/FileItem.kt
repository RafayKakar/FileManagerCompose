package com.example.filemanager.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class FileItem(
    var name: String,
    var path: String,
    var type: String,
    var size: String,
    var lastModifiedTime: String,
    var lastModifiedTimeLong: Long
)