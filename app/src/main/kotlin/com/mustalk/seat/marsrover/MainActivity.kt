package com.mustalk.seat.marsrover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mustalk.seat.marsrover.presentation.navigation.MarsRoverNavigation
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install system splash screen with Mars theme
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MarsRoverTheme {
                MarsRoverApp()
            }
        }
    }
}

/**
 * Main app composable that sets up navigation and overall app structure.
 */
@Composable
fun MarsRoverApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        MarsRoverNavigation(
            navController = navController,
            contentPadding = innerPadding
        )
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun MarsRoverAppLight() {
    MarsRoverTheme(darkTheme = false) {
        MarsRoverApp()
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun MarsRoverAppDark() {
    MarsRoverTheme(darkTheme = true) {
        MarsRoverApp()
    }
}
