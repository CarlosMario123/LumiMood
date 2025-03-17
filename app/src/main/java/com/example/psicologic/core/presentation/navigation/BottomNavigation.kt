package com.example.psicologic.core.presentation.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination


data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: NavigationRoutes
)

/**
 * Composable function that creates the bottom navigation bar
 *
 * @param items List of navigation items to display
 * @param navController Navigation controller to handle navigation
 * @param currentRoute Current route to determine which item is selected
 */
@Composable
fun PsicologicBottomNavigation(
    items: List<BottomNavItem>,
    navController: NavController,
    currentRoute: String?
) {
    BottomNavigation {
        items.forEach { item ->
            val selected = currentRoute == item.route.route

            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route.route) {
                        navController.navigate(item.route.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}