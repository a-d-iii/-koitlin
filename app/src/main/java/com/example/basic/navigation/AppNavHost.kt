package com.example.basic.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.basic.AttendanceScreen
import com.example.basic.FoodMenuScreen
import com.example.basic.HomeScreen
import com.example.basic.MoreScreen
import com.example.basic.PlannerScreen

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Planner : Screen("planner", "Planner", Icons.Filled.CalendarToday)
    object Attendance : Screen("attendance", "Attendance", Icons.Filled.CheckCircle)
    object Food : Screen("food", "Food", Icons.Filled.Restaurant)
    object More : Screen("more", "More", Icons.Filled.MoreHoriz)
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Planner, Screen.Attendance, Screen.Food, Screen.More)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            composable(Screen.Attendance.route) { AttendanceScreen() }
            composable(Screen.Food.route) {
                FoodMenuScreen(onShowSummary = {}, onViewMonth = {})
            }
            composable(Screen.More.route) { MoreScreen() }
        }
    }
}
