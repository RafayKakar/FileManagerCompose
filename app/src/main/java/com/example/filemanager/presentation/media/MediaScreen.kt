package com.example.filemanager.presentation.media

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.presentation.commons.FileListItem
import com.example.filemanager.presentation.commons.ItemListComponent
import com.example.filemanager.presentation.commons.shimmerLoadingAnimation

@Composable
fun MediaScreen(
    mediaScreenViewModel: MediaScreenViewModel = hiltViewModel()
) {
    val mediaFilesList = mediaScreenViewModel.mediaFiles.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header for recent files
        Text(text = "Recents", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Observe and handle loading state
        when {
            mediaFilesList.loadState.refresh is LoadState.Loading -> {
                // Initial load - show shimmer or loading indicator
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) { // Show 5 loading placeholders
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp)) // Replace with your custom shimmer composable
                    }
                }
            }

            mediaFilesList.loadState.append is LoadState.Loading -> {
                // Appending more data to the list - show a loading indicator at the bottom
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mediaFilesList.itemSnapshotList.items) { file ->
                        file?.let {
                            ItemListComponent(fileItem = it)
                        }
                    }
                    // Loading indicator for loading more items
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
            mediaFilesList.loadState.refresh is LoadState.Error -> {
                // Handle error state during initial load
                val error = (mediaFilesList.loadState.refresh as LoadState.Error).error
                Text(
                    text = "Error loading data: ${error.localizedMessage}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // Display actual data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    items(mediaFilesList.itemSnapshotList.items) { file ->
                        file?.let {
                            ItemListComponent(fileItem = it)
                        }
                    }
                }
            }
        }
    }
}
