package com.example.randomqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Alpha,
                    Screen.Beta,
                    Screen.Gamma,
                    Screen.Delta
                )
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.label) },
                                    selected = currentRoute == screen.route,
                                    onClick = { navController.navigate(screen.route) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(navController, startDestination = Screen.Alpha.route, modifier = Modifier.padding(padding)) {
                        composable(Screen.Alpha.route) { PageScreen("Alpha Page") }
                        composable(Screen.Beta.route) { PageScreen("Beta Page") }
                        composable(Screen.Gamma.route) { PageScreen("Gamma Page") }
                        composable(Screen.Delta.route) { PageScreen("Delta Page") }
                    }
                }
            }
        }
    }
}

enum class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Alpha("alpha", "Alpha", Icons.Default.Home),
    Beta("beta", "Beta", Icons.Default.List),
    Gamma("gamma", "Gamma", Icons.Default.Favorite),
    Delta("delta", "Delta", Icons.Default.Settings)
}

@Composable
fun PageScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PageScreen("Preview")
}
