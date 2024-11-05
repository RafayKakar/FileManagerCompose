package com.example.filemanager.presentation.permissionll

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.filemanager.R
import com.example.filemanager.presentation.initial_screen_navigator.InitialScreenEvent
import com.example.filemanager.utils.MyEventListener
import com.example.filemanager.utils.openAppSettings


@Composable
fun PermissionScreen(
    onEvent: (InitialScreenEvent) -> Unit,
    onNavigateToDashboardEvent: () -> Unit,
    context: Context = LocalContext.current,
) {

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3F)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.storage_permission_image),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.allow_storage_permission),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            textAlign = TextAlign.Start,
            style = TextStyle
                .Default.copy(
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Default,
                    fontSize = 15.sp,
                )
        )
        Column() {
            Button(
                onClick = {
                    openAppSettings(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.allow_permission),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            Button(
                onClick = {
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.not_now),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }

    MyEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                if (Environment.isExternalStorageManager()) {
                    onEvent.invoke(InitialScreenEvent.SaveAppEntry)
                    onNavigateToDashboardEvent.invoke()
                }
            }

            else -> {}
        }
    }
}


@Preview
@Composable
fun ShowPermissionScreen() {
    com.example.filemanager.ui.theme.FileManagerTheme {
        PermissionScreen({},{})
    }
}