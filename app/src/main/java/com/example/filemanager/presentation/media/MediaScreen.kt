package com.example.filemanager.presentation.media

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.presentation.commons.FileListItem
import com.example.filemanager.presentation.commons.ItemListComponent

@Composable
fun MediaScreen(
    mediaScreenViewModel: MediaScreenViewModel = hiltViewModel()
) {
    // Collect the list of media files from the ViewModel as a state
    val mediaFilesList by mediaScreenViewModel.mediafiles.collectAsState()

    // State to track the LazyColumn's scroll position
    val listState = rememberLazyListState()

    // Track the loading state from the ViewModel
    val isLoading by mediaScreenViewModel.isLoading.collectAsState()

    // Trigger loading more data when reaching the end of the list
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                lastVisibleItemIndex?.let {
                    Log.d("MediaFilesIndex", "Last visible item index: $it")
                    // Check if the last visible item is the last in the list and more data isn't already loading
                    if (it == mediaFilesList.size - 1 && !isLoading) {
                        mediaScreenViewModel.loadNextPage()
                    }
                }
            }
    }

    // UI layout with a column and lazy list
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header for recent files
        Text(text = "Recents", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn for displaying the list of media files
        LazyColumn(
            state = listState, // Attach the list state to the LazyColumn
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(30.dp) // Space between items
        ) {
            items(mediaFilesList) { file ->
                ItemListComponent(fileItem = file)
//                FileListItem(file)
            }
        }
    }
}