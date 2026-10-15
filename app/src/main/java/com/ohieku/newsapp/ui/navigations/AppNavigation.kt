package com.ohieku.newsapp.ui.navigations

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ohieku.newsapp.R
import com.ohieku.newsapp.ui.bookmarks.compose.renderBookmarksScreen
import com.ohieku.newsapp.ui.home.compose.renderHomeScreen
import com.ohieku.newsapp.ui.search.compose.renderSearchScreen

private enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(Routes.HOME_SCREEN, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    Saved(Routes.BOOKMARKS_SCREEN, R.string.nav_saved, Icons.Filled.Bookmarks, Icons.Outlined.Bookmarks),
}

@Composable
fun AppNavigationGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(currentRoute = currentRoute) { destination ->
                    navController.navigate(destination.route) {
                        // Keep each tab's screen (scroll position, loaded pages) when switching tabs
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_SCREEN,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME_SCREEN) {
                renderHomeScreen(onSearchTapped = { navController.navigate(Routes.SEARCH_SCREEN) })
            }
            composable(Routes.SEARCH_SCREEN) {
                renderSearchScreen(onBackTapped = { navController.popBackStack() })
            }
            composable(Routes.BOOKMARKS_SCREEN) {
                renderBookmarksScreen()
            }
        }
    }
}

@Composable
private fun AppBottomBar(currentRoute: String?, onDestinationSelected: (TopLevelDestination) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val selected = destination.route == currentRoute
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(label) },
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
