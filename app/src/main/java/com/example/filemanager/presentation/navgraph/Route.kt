package com.example.filemanager.presentation.navgraph


sealed class Route(val route: String) {
    object AppStartNavigation : Route(route = "appStartNavigation")
    object InitialScreensNavigator : Route("initialScreensNavigator")
    object SplashScreen : Route("splashScreen")
    object PermissionScreen : Route("permissionScreen")
    object DashboardNavigation : Route("dashboardNavigation")
    object DashboardScreensNavigator : Route("dashboardScreensNavigator")
    object HomeScreenNavigator : Route("homeScreensNavigator")
    object Recents : Route("recents")
    object Media : Route("media")
    object Home : Route("home")
    object Documents : Route("documents")
    object Others : Route("others")
}