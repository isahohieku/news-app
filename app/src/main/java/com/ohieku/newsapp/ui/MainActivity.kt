package com.ohieku.newsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ohieku.newsapp.ui.navigations.AppNavigationGraph
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install the splash screen
        installSplashScreen()

        // Set the content to use Jetpack Compose
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                AppEntryPoint(navController)
            }
        }
    }
}


@Composable
fun AppEntryPoint(navController: NavHostController) {
    AppNavigationGraph(navController)
}
