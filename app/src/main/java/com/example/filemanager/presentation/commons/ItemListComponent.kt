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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ItemListComponent(
    fileItem: FileItem,
    context: Context = LocalContext.current
) {

    val imageLoading = remember {
        mutableStateOf(true)
    }
    val fileImageIcon = remember{
        mutableStateOf<Bitmap?>(null)
    }


    LaunchedEffect(Unit) {
        val imageBitmap = try {

            withContext(Dispatchers.IO){
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true // Only load image dimensions
                }
                BitmapFactory.decodeFile(fileItem.path, options)

                options.inSampleSize = calculateInSampleSize(options, 60, 60) // Target size, adjust as needed
                options.inJustDecodeBounds = false // Load the actual bitmap

                // Decode bitmap with calculated sample size
                BitmapFactory.decodeFile(fileItem.path, options)
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
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(60.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                        false -> {
                            fileImageIcon.value?.let { fileIcon ->
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = fileIcon).apply(block = fun ImageRequest.Builder.() {
                                            }).build()
                                    ),
                                    contentDescription = "file_i    con", // Provide content description as needed
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(5.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Top),
                                    contentScale = ContentScale.Crop
                                )
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



fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}