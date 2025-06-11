package com.mustalk.seat.marsrover.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.core.ui.components.MarsCard
import com.mustalk.seat.marsrover.core.ui.components.MarsLottieLoader
import com.mustalk.seat.marsrover.core.ui.components.MarsToast
import com.mustalk.seat.marsrover.core.ui.components.MarsToastType
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import com.mustalk.seat.marsrover.feature.dashboard.components.MissionResultCard
import com.mustalk.seat.marsrover.feature.dashboard.components.NewMissionFab
import com.mustalk.seat.marsrover.core.ui.R as CoreUiR

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
fun DashboardContent(
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
            // Add Mars background image
            Image(
                painter = painterResource(id = CoreUiR.drawable.core_ui_mars_background),
                contentDescription = stringResource(CoreUiR.string.core_ui_cd_mars_background),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when {
                uiState.isLoading -> {
                    MarsLottieLoader(
                        message = stringResource(CoreUiR.string.core_ui_loading_mission_data),
                        isVisible = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.lastMissionResult != null -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Welcome header
                        WelcomeHeader()

                        // Mission result card
                        MissionResultCard(
                            missionResult = uiState.lastMissionResult,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Add bottom padding to avoid FAB overlap
                        Spacer(modifier = Modifier.height(Constants.UI.FAB_BOTTOM_PADDING_DP.dp))
                    }
                }

                else -> {
                    // Empty state - no missions yet
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyDashboardState(
                            onNewMissionClick = onNewMissionClick
                        )
                    }
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
                        title = stringResource(CoreUiR.string.core_ui_toast_mission_failed),
                        message = error,
                        type = MarsToastType.Error,
                        onClick = onErrorDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Auto-dismiss error after showing
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(Constants.UI.ERROR_MESSAGE_DURATION_MS)
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
            text = stringResource(R.string.feature_dashboard_welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.feature_dashboard_mission_control),
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MarsCard(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .then(
                        if (isLandscape) {
                            Modifier.fillMaxWidth(Constants.UI.LANDSCAPE_CARD_WIDTH_FRACTION)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    ).clickable { onNewMissionClick() },
            contentDescription = stringResource(R.string.feature_dashboard_welcome_screen_tap)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.feature_dashboard_welcome_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = stringResource(R.string.feature_dashboard_mission_control),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.feature_dashboard_empty_title),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.feature_dashboard_empty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
