package com.example.filemanager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.filemanager.domain.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.w3c.dom.Document
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Context.log(data: String) {
    Log.d("hm1", "log: ${data}")
}

fun Context.toast(data: String) {
    Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
}

fun Context.compressBitmapWithoutSizeLoss(imageBitmap: Bitmap): Bitmap {
    //converted into webp into lowest quality
    val stream = ByteArrayOutputStream()
    imageBitmap.compress(Bitmap.CompressFormat.JPEG,0,stream);//0=lowest, 100=highest quality
    val byteArray = stream.toByteArray();
    //convert your byteArray into bitmap
    val compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size);
    return compressedBitmap;
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.setData(uri)
    context.startActivity(intent)
}


fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        navController.graph.startDestinationRoute?.let { screen_route ->
            popUpTo(screen_route) {
                saveState = true
            }
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

fun isFileEmptyHiddenOrCache(file: File, context: Context): Triple<Boolean, Boolean, Boolean> {
    val isFileEmpty = file.length() == 0L
    val isFileHidden = file.isHidden
    val isFileCache = try {
        val cacheDir = context.cacheDir
        val canonicalCacheDir = cacheDir.canonicalPath
        val canonicalFilePath = file.canonicalPath

        canonicalFilePath.startsWith(canonicalCacheDir)
    } catch (e: IOException) {
        false
    }

    return Triple(isFileEmpty, isFileHidden, isFileCache)
}

@Composable
fun MyEventListener(OnEvent: (event: Lifecycle.Event) -> Unit) {

    val eventHandler = rememberUpdatedState(newValue = OnEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { source, event ->
            eventHandler.value(event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}


fun Date.formatToDMY(): String {
    val df = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    return df.format(this)
}

fun getFormattedTime(lastModifiedTime: Long): String {
    return lastModifiedTime.millisToDate().formatToDMY()
}

fun Long.millisToDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this * 1000
    return calendar.time
}

fun Long.getDate(): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    return sdf.format(this)
}

fun Context.sizeFormatter(size: Long): String =
    android.text.format.Formatter.formatShortFileSize(this, size)

fun Modifier.testLine(color: Color = Color.Red) = border(
    width = 2.dp, color = color, shape = RectangleShape
)
