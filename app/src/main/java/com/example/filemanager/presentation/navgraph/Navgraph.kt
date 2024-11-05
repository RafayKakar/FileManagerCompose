package com.example.filemanager.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.filemanager.presentation.dashboard_navigator.DashboardScreensNavigator
import com.example.filemanager.presentation.initial_screen_navigator.InitialScreensNavigator

@Composable
fun Navgraph(startDestination: String, onEvent: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.InitialScreensNavigator.route
        ) {
            composable(route = Route.InitialScreensNavigator.route) {
                InitialScreensNavigator() {
                    onEvent.invoke()
                }
            }
        }

        navigation(
            route = Route.DashboardNavigation.route,
            startDestination = Route.DashboardScreensNavigator.route
        ) {
            composable(route = Route.DashboardScreensNavigator.route) {
                DashboardScreensNavigator()
            }
        }

    }


}