package com.ohieku.newsapp.ui.navigations

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors
import com.ohieku.newsapp.ui.bookmarks.compose.renderBookmarksScreen
import com.ohieku.newsapp.ui.home.compose.renderHomeScreen
import com.ohieku.newsapp.ui.search.compose.renderSearchScreen

private const val REQUESTED_CATEGORY_KEY = "requested_category"

private enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(Routes.HOME_SCREEN, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    Saved(Routes.BOOKMARKS_SCREEN, R.string.nav_saved, Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
}

@Composable
fun AppNavigationGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }
    var homeReselections by rememberSaveable { mutableIntStateOf(0) }

    fun openTab(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            // Keep each tab's screen (scroll position, loaded pages) when switching tabs
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(currentRoute = currentRoute) { destination ->
                    if (destination == TopLevelDestination.Home && currentRoute == Routes.HOME_SCREEN) {
                        homeReselections++
                    } else {
                        openTab(destination)
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
            composable(Routes.HOME_SCREEN) { entry ->
                val requestedCategory by entry.savedStateHandle
                    .getStateFlow<String?>(REQUESTED_CATEGORY_KEY, null)
                    .collectAsStateWithLifecycle()
                renderHomeScreen(
                    onSearchTapped = { navController.navigate(Routes.SEARCH_SCREEN) },
                    onOpenSaved = { openTab(TopLevelDestination.Saved) },
                    scrollToTopSignal = homeReselections,
                    requestedCategory = requestedCategory?.let { name ->
                        NewsCategory.entries.firstOrNull { it.name == name }
                    },
                    onRequestedCategoryHandled = { entry.savedStateHandle[REQUESTED_CATEGORY_KEY] = null }
                )
            }
            composable(Routes.SEARCH_SCREEN) {
                renderSearchScreen(
                    onBackTapped = { navController.popBackStack() },
                    onOpenSaved = { openTab(TopLevelDestination.Saved) },
                    onBrowseCategory = { category ->
                        // Hand the category back to Home, which switches to it on return
                        navController.previousBackStackEntry?.savedStateHandle?.set(REQUESTED_CATEGORY_KEY, category.name)
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.BOOKMARKS_SCREEN) {
                renderBookmarksScreen()
            }
        }
    }
}

@Composable
private fun AppBottomBar(currentRoute: String?, onDestinationSelected: (TopLevelDestination) -> Unit) {
    // Compact bar: M3 NavigationBar enforces an 80dp min height, which felt oversized here
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)) {
            HorizontalDivider(color = LocalNewsColors.current.hairline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .selectableGroup()
            ) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = destination.route == currentRoute
                    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(
                                selected = selected,
                                onClick = { onDestinationSelected(destination) },
                                role = Role.Tab
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(destination.labelRes),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, lineHeight = 13.sp, letterSpacing = 0.2.sp),
                            color = tint
                        )
                    }
                }
            }
        }
    }
}
