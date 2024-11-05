package com.example.filemanager.presentation

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.filemanager.R
import com.example.filemanager.presentation.initial_screen_navigator.InitialScreenEvent
import com.example.filemanager.ui.theme.FileManagerTheme
import com.example.filemanager.presentation.navgraph.Route
import com.example.filemanager.utils.navigateToTab

@Composable
fun SplashScreen(navController: NavHostController, onEvent: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.splash_screen_image),
                contentDescription = null,
                modifier = Modifier
                    .height(250.dp)
                    .width(250.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        }

        Button(modifier = Modifier
            .width(200.dp)
            .height(50.dp), onClick = {
            if (Environment.isExternalStorageManager()) {
                onEvent.invoke()
            } else {
                navigateToTab(navController, Route.PermissionScreen.route)
            }
        }) {
            Text(
                text = stringResource(id = R.string.lets_start),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}


