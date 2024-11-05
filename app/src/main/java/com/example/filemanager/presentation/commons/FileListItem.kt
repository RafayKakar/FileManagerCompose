package com.example.filemanager.presentation.commons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.ui.theme.FileManagerTheme

@Composable
fun FileListItem(fileItem: FileItem) {

    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current).data(fileItem.path).build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop
    )

    Text(
        text = fileItem.name, modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )

}

@Preview
@Composable
fun showItem() {
    FileManagerTheme {
//        FileListItem(fileItem = FileItem("dfd", "File", 0L, 0L, null!!))
    }
}