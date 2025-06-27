package com.example.basic.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.basic.AttendanceScreen
import com.example.basic.FoodMenuScreen
import com.example.basic.FoodSummaryScreen
import com.example.basic.MonthlyMenuScreen
import com.example.basic.AttendanceDetailsScreen
import com.example.basic.HomeScreen
import com.example.basic.MoreScreen
import com.example.basic.PlannerScreen

 
sealed class Screen(
    val route: String,
    val label: String,
    val filledIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : Screen(
        "home",
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home
    )
    object Planner : Screen(
        "planner",
        "Planner",
        Icons.Filled.CalendarToday,
        Icons.Outlined.CalendarToday
    )
    object Attendance : Screen(
        "attendance",
        "Attendance",
        Icons.Filled.CheckCircle,
        Icons.Outlined.CheckCircle
    )
    object Food : Screen(
        "food",
        "Food",
        Icons.Filled.Fastfood,
        Icons.Outlined.Fastfood
    )
    object More : Screen(
        "more",
        "More",
        Icons.Filled.MoreHoriz,
        Icons.Outlined.MoreHoriz
    )

    // Additional screens that are not part of the bottom navigation bar
    // but still need a route entry for navigation.
    object FoodSummary : Screen(
        "foodSummary",
        "Food Summary",
        Icons.Filled.Fastfood,
        Icons.Outlined.Fastfood
    )

    object MonthlyMenu : Screen(
        "monthlyMenu",
        "Monthly Menu",
        Icons.Filled.Fastfood,
        Icons.Outlined.Fastfood
    )

    object AttendanceDetails : Screen(
        "attendanceDetails",
        "Attendance Details",
        Icons.Filled.CheckCircle,
        Icons.Outlined.CheckCircle
    )

}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Planner, Screen.Attendance, Screen.Food, Screen.More)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            val iconImage = if (selected) screen.filledIcon else screen.outlinedIcon
                            val iconModifier = if (screen == Screen.Home) Modifier.size(32.dp) else Modifier
                            Icon(iconImage, contentDescription = screen.label, modifier = iconModifier)
                        },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            when (screen) {
                                Screen.Food -> {
                                    if (currentRoute == Screen.MonthlyMenu.route || currentRoute == Screen.FoodSummary.route) {
                                        navController.popBackStack(Screen.Food.route, false)
                                    } else {
                                        navController.navigate(Screen.Food.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        }
                                    }
                                }
                                Screen.Attendance -> {
                                    if (currentRoute == Screen.AttendanceDetails.route) {
                                        navController.popBackStack(Screen.Attendance.route, false)
                                    } else {
                                        navController.navigate(Screen.Attendance.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        }
                                    }
                                }
                                else -> {
                                    navController.navigate(screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Planner.route) { PlannerScreen() }
            composable(Screen.Attendance.route) {
                AttendanceScreen(onShowDetails = { navController.navigate(Screen.AttendanceDetails.route) })
            }
            composable(Screen.Food.route) {
                FoodMenuScreen(
                    onShowSummary = { navController.navigate(Screen.FoodSummary.route) },
                    onViewMonth = { navController.navigate(Screen.MonthlyMenu.route) }
                )
            }
            composable(Screen.FoodSummary.route) {
                FoodSummaryScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.MonthlyMenu.route) {
                MonthlyMenuScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.AttendanceDetails.route) {
                AttendanceDetailsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.More.route) { MoreScreen() }
        }
    }
}
