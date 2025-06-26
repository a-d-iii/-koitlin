package com.example.basic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basic.AttendanceScreen
import com.example.basic.FoodMenuScreen
import com.example.basic.HomeScreen
import com.example.basic.PlannerScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Planner : Screen("planner")
    object Attendance : Screen("attendance")
    object Food : Screen("food")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Planner.route) { PlannerScreen() }
        composable(Screen.Attendance.route) { AttendanceScreen() }
        composable(Screen.Food.route) {
            FoodMenuScreen(onShowSummary = {}, onViewMonth = {})
        }
    }
}
