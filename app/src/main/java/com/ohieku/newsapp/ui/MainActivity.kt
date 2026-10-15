package com.ohieku.newsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.home.HomeViewModel
import com.ohieku.newsapp.ui.navigations.AppNavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen BEFORE super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplashOnScreen = true
        lifecycleScope.launch {
            withTimeoutOrNull(2000L) {
                homeViewModel.uiState.first { !it.feed.isLoading }
            }
            keepSplashOnScreen = false
        }

        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        // Set the content to use Jetpack Compose
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                AppEntryPoint(navController, homeViewModel)
            }
        }
    }
}

@Composable
fun AppEntryPoint(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    AppNavigationGraph(navController, homeViewModel)
}
