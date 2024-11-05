package com.example.filemanager.presentation.others

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filemanager.presentation.media.MediaScreenViewModel

@Composable
fun OthersScreen(
    mediaScreenViewModel: MediaScreenViewModel = hiltViewModel()
) {

    val mediaFilesList by mediaScreenViewModel.mediafiles.collectAsState()

}