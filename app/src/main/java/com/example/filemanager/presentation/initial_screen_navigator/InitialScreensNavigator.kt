package com.example.filemanager.presentation.initial_screen_navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.presentation.navgraph.Route
import com.example.filemanager.presentation.permissionll.PermissionScreen
import com.example.filemanager.presentation.SplashScreen

@Composable
fun InitialScreensNavigator(onEvent: () -> Unit) {

    val initialScreenNavController = rememberNavController()
    val initialScreenViewModel: InitialScreenViewModel = hiltViewModel()

    NavHost(
        navController = initialScreenNavController,
        startDestination = Route.SplashScreen.route
    ) {

        composable(route = Route.SplashScreen.route) {
            SplashScreen(initialScreenNavController, {
                onEvent.invoke()
            })
        }

        composable(route = Route.PermissionScreen.route) {
            PermissionScreen(
                initialScreenViewModel::onEvent, {
                    onEvent.invoke()
                }, LocalContext.current)
        }

    }

}


