package com.example.filemanager.presentation.dashboard_navigator

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.filemanager.R
import com.example.filemanager.presentation.dashboard_navigator.components.FilesBottomNavigation
import com.example.filemanager.presentation.documents.DocumentsScreen
import com.example.filemanager.presentation.documents.DocumentsViewModel
import com.example.filemanager.presentation.home.HomeScreen
import com.example.filemanager.presentation.initial_screen_navigator.InitialScreenViewModel
import com.example.filemanager.presentation.media.MediaScreen
import com.example.filemanager.presentation.navgraph.Navgraph
import com.example.filemanager.presentation.navgraph.Route
import com.example.filemanager.presentation.others.OthersScreen
import com.example.filemanager.presentation.recents.RecentsScreen
import com.example.filemanager.presentation.recents.RecentsViewModel
import com.example.filemanager.utils.navigateToTab

@Composable
fun DashboardScreensNavigator() {

    val dashboardNavController = rememberNavController()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val backStackState = dashboardNavController.currentBackStackEntryAsState().value

    var selectedItem by rememberSaveable {
        mutableStateOf(0)
    }
    selectedItem = when (backStackState?.destination?.route) {
        Route.Recents.route -> 0
        Route.Documents.route -> 1
        Route.Home.route -> 2
        Route.Media.route -> 3
        Route.Others.route -> 4
        else -> 0
    }

    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = R.drawable.recents_dashboard, text = "recents"),
            BottomNavigationItem(icon = R.drawable.documents_dashboard, text = "documents"),
            BottomNavigationItem(icon = R.drawable.home_dashboard, text = "home"),
            BottomNavigationItem(icon = R.drawable.media_dashboard, text = "media"),
            BottomNavigationItem(icon = R.drawable.others_dashboard, text = "others"),
        )
    }


    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        FilesBottomNavigation(
            items = bottomNavigationItems,
            selectedItem = selectedItem,
            onItemClick = { index ->
                when (index) {

                    0 -> navigateToTab(
                        navController = dashboardNavController,
                        route = Route.Recents.route
                    )

                    1 -> navigateToTab(
                        navController = dashboardNavController,
                        route = Route.Documents.route
                    )

                    2 -> navigateToTab(
                        navController = dashboardNavController,
                        route = Route.Home.route
                    )

                    3 -> navigateToTab(
                        navController = dashboardNavController,
                        route = Route.Media.route
                    )

                    4 -> navigateToTab(
                        navController = dashboardNavController,
                        route = Route.Others.route
                    )

                }
            }
        )
    }) {
        val bottomPadding = it.calculateBottomPadding()

        NavHost(
            navController = dashboardNavController,
            startDestination = Route.Recents.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(route = Route.Recents.route) { backStackEntry ->
                OnBackClickStateSaver(dashboardNavController)
                RecentsScreen()
            }
            composable(route = Route.Documents.route) { backStackEntry ->
                OnBackClickStateSaver(dashboardNavController)
                DocumentsScreen()
            }
            composable(route = Route.Home.route) { backStackEntry ->
                OnBackClickStateSaver(dashboardNavController)
                HomeScreen()
            }
            composable(route = Route.Media.route) { backStackEntry ->
                OnBackClickStateSaver(dashboardNavController)
                MediaScreen()
            }
            composable(route = Route.Others.route) { backStackEntry ->
                OnBackClickStateSaver(dashboardNavController)
                OthersScreen()
            }

        }
    }
}

@Composable
fun OnBackClickStateSaver(navController: NavController) {
    BackHandler(true) {
        navigateToTab(
            navController = navController,
            route = Route.Home.route
        )
    }
}


data class BottomNavigationItem(
    @DrawableRes val icon: Int,
    val text: String
)