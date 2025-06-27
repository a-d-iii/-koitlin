package com.example.basic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.basic.navigation.AppNavHost
import com.example.basic.ui.theme.VitStudentAppTheme
import com.example.basic.ScaledLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VitStudentAppTheme {
                ScaledLayout {
                    AppNavHost()
                }
            }
        }
    }
}
