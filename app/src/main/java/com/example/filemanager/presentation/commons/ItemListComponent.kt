package com.example.filemanager.presentation.commons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.filemanager.R
import com.example.filemanager.domain.model.FileItem
import com.example.filemanager.utils.compressBitmapWithoutSizeLoss
import com.example.filemanager.utils.log

@Composable
fun ItemListComponent(
    fileItem: FileItem,
    context: Context = LocalContext.current
) {
    val imageLoading = remember {
        mutableStateOf(true)
    }
    val fileImageIcon = remember {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(Unit) {
        val imageBitmap = try {
            BitmapFactory.decodeFile(fileItem.path).apply {
                byteCount
            }
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, R.drawable.human_image_1)
        }
        fileImageIcon.value = imageBitmap
        imageLoading.value = false
    }
    Box (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp
                )
                .align(Alignment.Center)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .drawBehind {
                        val width = size.width
                        val height = size.height
                        drawLine(
                            color = Color("#C4E1F6".toColorInt()),
                            start = Offset(
                                x = 0f,
                                y = height
                            ),
                            end = Offset(
                                x = width,
                                y = height + 2f
                            ),
                            strokeWidth = 2f,
                            cap = StrokeCap.Round,
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    when (imageLoading.value) {
                        true -> {
//                            CircularProgressIndicator(
//                                modifier = Modifier
//                                    .padding(5.dp)
//                                    .size(60.dp),
//                                color = MaterialTheme.colorScheme.secondary,
//                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
//                            )
                        }
                        false -> {
                            fileImageIcon.value?.let { fileIcon ->
//                                Image(
//                                )
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = fileIcon).apply(block = fun ImageRequest.Builder.() {
                                            }).build()
                                    ),
                                    contentDescription = "file_icon", // Provide content description as needed
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(5.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Top),
                                    contentScale = ContentScale.Crop
                                )
//                                 AsyncImage(
//                                    model = ImageRequest.Builder(LocalContext.current)
//                                        .data(fileIcon)
//                                        .build(),
//                                    contentDescription = null,
//                                    modifier = Modifier
//                                        .size(60.dp)
//                                        .padding(5.dp)
//                                        .clip(CircleShape)
//                                        .align(Alignment.Top),
//                                    contentScale = ContentScale.Crop,
//                                )
                            }
                        }
                    }

                    Text(
                        text = fileItem.name,
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f),
                        color = Color.Black,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    )
                    Icon(
                        modifier = Modifier
                            .padding(
                                end = 12.dp
                            )
                            .size(18.dp),
                        painter = painterResource(id = R.drawable.dots_vertical_svgrepo_com),
                        contentDescription = "file_options_description",
                        tint = Color("#C4E1F6".toColorInt())
                    )
//                    AsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(R.drawable.dots_vertical_svgrepo_com)
//                            .build(),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(
//                                end = 12.dp
//                            )
//                            .size(18.dp),
//                        contentScale = ContentScale.Crop,
//                    )
                }
                Text(
                    text = fileItem.lastModifiedTime,
                    modifier = Modifier
                        .padding(end = 15.dp, bottom = 10.dp)
                        .wrapContentSize()
                        .align(Alignment.End),
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}