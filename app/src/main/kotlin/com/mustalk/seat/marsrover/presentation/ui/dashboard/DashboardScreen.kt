@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.presentation.ui.components.MarsFullScreenLoader
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToast
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToastType
import com.mustalk.seat.marsrover.presentation.ui.dashboard.components.MissionResultCard
import com.mustalk.seat.marsrover.presentation.ui.dashboard.components.NewMissionFab
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

private const val DASHBOARD_ERROR_DISPLAY_DURATION_MS = 5000L

/**
 * Main Dashboard screen that displays mission results and provides access to create new missions.
 * This is the primary screen users see after the splash screen.
 */
@Composable
fun DashboardScreen(
    onNavigateToNewMission: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation to new mission screen
    LaunchedEffect(uiState.showNewMissionDialog) {
        if (uiState.showNewMissionDialog) {
            onNavigateToNewMission()
            viewModel.onNewMissionDialogDismissed()
        }
    }

    DashboardContent(
        uiState = uiState,
        onNewMissionClick = viewModel::onNewMissionClicked,
        onErrorDismiss = viewModel::clearError,
        modifier = modifier
    )
}

@Composable
internal fun DashboardContent(
    uiState: DashboardUiState,
    onNewMissionClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            NewMissionFab(
                onClick = onNewMissionClick,
                extended = uiState.lastMissionResult == null
            )
        }
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    MarsFullScreenLoader(
                        message = stringResource(R.string.loading_mission_data)
                    )
                }

                uiState.lastMissionResult != null -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Welcome header
                        WelcomeHeader()

                        // Mission result card
                        MissionResultCard(
                            missionResult = uiState.lastMissionResult,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                else -> {
                    // Empty state - no missions yet
                    EmptyDashboardState(
                        onNewMissionClick = onNewMissionClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Error toast overlay
            uiState.errorMessage?.let { error ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                ) {
                    MarsToast(
                        title = stringResource(R.string.toast_mission_failed),
                        message = error,
                        type = MarsToastType.Error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Auto-dismiss error after showing
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(DASHBOARD_ERROR_DISPLAY_DURATION_MS)
                    onErrorDismiss()
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Mission Control Dashboard",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun EmptyDashboardState(
    onNewMissionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(32.dp)
                .clickable { onNewMissionClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Mission Control Dashboard",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Ready to deploy your first Mars rover mission?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = "Tap the + button to get started",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(name = "Dashboard - Empty State", showBackground = true)
@Composable
private fun DashboardEmptyPreview() {
    MarsRoverTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onNewMissionClick = { },
            onErrorDismiss = { }
        )
    }
}

@Preview(name = "Dashboard - With Mission Result", showBackground = true)
@Composable
private fun DashboardWithResultPreview() {
    MarsRoverTheme {
        DashboardContent(
            uiState =
                DashboardUiState(
                    lastMissionResult =
                        MissionResult(
                            finalPosition = "1 3 N",
                            isSuccess = true,
                            originalInput = """{"topRightCorner": {"x": 5, "y": 5}}"""
                        )
                ),
            onNewMissionClick = { },
            onErrorDismiss = { }
        )
    }
}

@Preview(name = "Dashboard - Loading", showBackground = true)
@Composable
private fun DashboardLoadingPreview() {
    MarsRoverTheme {
        DashboardContent(
            uiState = DashboardUiState(isLoading = true),
            onNewMissionClick = { },
            onErrorDismiss = { }
        )
    }
}

@Preview(name = "Dashboard - Error", showBackground = true)
@Composable
private fun DashboardErrorPreview() {
    MarsRoverTheme {
        DashboardContent(
            uiState =
                DashboardUiState(
                    errorMessage = "Failed to process mission data"
                ),
            onNewMissionClick = { },
            onErrorDismiss = { }
        )
    }
}
