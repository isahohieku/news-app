package com.ohieku.newsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ohieku.newsapp.ui.common.StartupReadiness
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.navigations.AppNavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var startupReadiness: StartupReadiness

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen BEFORE super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplashOnScreen = true
        lifecycleScope.launch {
            withTimeoutOrNull(SPLASH_TIMEOUT_MS) {
                startupReadiness.isReady.first { it }
            }
            keepSplashOnScreen = false
        }

        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        // Set the content to use Jetpack Compose
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                AppEntryPoint(navController)
            }
        }
    }

    companion object {
        private const val SPLASH_TIMEOUT_MS = 2000L
    }
}

@Composable
fun AppEntryPoint(navController: NavHostController) {
    AppNavigationGraph(navController)
}
