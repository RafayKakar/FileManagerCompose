package com.example.filemanager.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.filemanager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList


class FilesPagingSource(
   private var filesList: Flow<FileItem>
) : PagingSource<Int, FileItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FileItem> {
        val position = params.key ?: 1
        return try {
            var filesList=filesList.toList()
            LoadResult.Page(
                data = filesList,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (filesList.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FileItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}
