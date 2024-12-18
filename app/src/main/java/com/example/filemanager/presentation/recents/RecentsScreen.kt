package com.example.filemanager.presentation.recents

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.presentation.commons.FileListItem
import com.example.filemanager.presentation.commons.ItemListComponent


@Composable
fun RecentsScreen( recentsViewModel: RecentsViewModel = hiltViewModel()) {

    val recentFiles by recentsViewModel.recents.collectAsState()

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        Text(text = "Recents", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

//        Column (
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(30.dp)
//        ) {
//            recentFiles.map { file ->
//                ItemListComponent(fileItem = file)
//            }
////            items(recentFiles) { file ->
//////                FileListItem(file)
////            }
//        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(
                items = recentFiles.take(20),
                key = {
                    it.hashCode()
                }
            ) { file ->
                ItemListComponent(fileItem = file)
//                FileListItem(file)
            }
        }
    }

}