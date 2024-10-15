package com.ohieku.newsapp.ui.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ohieku.newsapp.ui.home.compose.renderHomeScreen

@Composable
fun AppNavigationGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {
        composable(Routes.HOME_SCREEN) {
            renderHomeScreen()
        }
    }
}
