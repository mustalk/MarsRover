package com.mustalk.seat.marsrover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import com.mustalk.seat.marsrover.feature.dashboard.DashboardContent
import com.mustalk.seat.marsrover.feature.dashboard.DashboardUiState
import com.mustalk.seat.marsrover.navigation.MarsRoverNavigation
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

    MarsRoverNavigation(
        navController = navController
    )
}

/**
 * Using DashboardContent on Previews instead of MarsRoverApp() because:
 * 1. MarsRoverApp() uses MarsRoverNavigation which requires hiltViewModel()
 * 2. Hilt ViewModels cannot be instantiated in Compose previews
 * 3. DashboardContent shows the actual app start state (EmptyDashboardState)
 * 4. This gives a realistic preview of what users see when opening the app
 */
@Preview(showBackground = true, name = "Light Theme")
@Composable
fun MarsRoverAppLight() {
    MarsRoverTheme(darkTheme = false) {
        DashboardContent(
            uiState = DashboardUiState(),
            onNewMissionClick = { /* Preview - no action */ },
            onErrorDismiss = { /* Preview - no action */ }
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun MarsRoverAppDark() {
    MarsRoverTheme(darkTheme = true) {
        DashboardContent(
            uiState = DashboardUiState(),
            onNewMissionClick = { /* Preview - no action */ },
            onErrorDismiss = { /* Preview - no action */ }
        )
    }
}
