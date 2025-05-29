@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.presentation.ui.components.MarsButton
import com.mustalk.seat.marsrover.presentation.ui.components.MarsButtonVariant
import com.mustalk.seat.marsrover.presentation.ui.components.MarsFullScreenLoader
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextField
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextFieldVariant
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToast
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToastType
import com.mustalk.seat.marsrover.presentation.ui.mission.components.IndividualInputsForm
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

private const val SUCCESS_MESSAGE_DURATION_MS = 5000L
private const val ERROR_MESSAGE_DURATION_MS = 7000L

/**
 * New Mission screen for creating and executing rover missions.
 * Supports both JSON input mode and individual field input mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMissionScreen(
    onNavigateBack: () -> Unit,
    onMissionCompleted: (String, Boolean, String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: NewMissionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle mission completion navigation - don't auto-navigate, let user see the result
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            // Extract result and notify parent but don't auto-navigate
            if (uiState.successMessage!!.contains("Final position:")) {
                val position = uiState.successMessage!!.substringAfter("Final position: ")
                val originalInput =
                    when (uiState.inputMode) {
                        InputMode.JSON -> uiState.jsonInput
                        InputMode.INDIVIDUAL -> {
                            // Build JSON from individual fields
                            """
                            {
                                "topRightCorner": {"x": ${uiState.plateauWidth}, "y": ${uiState.plateauHeight}},
                                "roverPosition": {"x": ${uiState.roverStartX}, "y": ${uiState.roverStartY}},
                                "roverDirection": "${uiState.roverStartDirection}",
                                "movements": "${uiState.movementCommands}"
                            }
                            """.trimIndent()
                        }
                    }
                onMissionCompleted(position, true, originalInput)
                // Removed auto-navigation - let user see the result and manually navigate
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.mission_new),
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
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
                        message = stringResource(R.string.loading_rover_commands)
                    )
                }

                else -> {
                    NewMissionContent(
                        uiState = uiState,
                        onInputModeChange = viewModel::switchInputMode,
                        onJsonInputChange = viewModel::updateJsonInput,
                        onPlateauWidthChange = viewModel::updatePlateauWidth,
                        onPlateauHeightChange = viewModel::updatePlateauHeight,
                        onRoverStartXChange = viewModel::updateRoverStartX,
                        onRoverStartYChange = viewModel::updateRoverStartY,
                        onRoverDirectionChange = viewModel::updateRoverStartDirection,
                        onMovementCommandsChange = viewModel::updateMovementCommands,
                        onExecuteMission = viewModel::executeMission,
                        onClearMessages = viewModel::clearMessages,
                        onNavigateBack = onNavigateBack,
                        onPlateauWidthFocusLost = viewModel::validatePlateauWidth,
                        onPlateauHeightFocusLost = viewModel::validatePlateauHeight,
                        onRoverStartXFocusLost = viewModel::validateRoverStartX,
                        onRoverStartYFocusLost = viewModel::validateRoverStartY,
                        onMovementCommandsFocusLost = viewModel::validateMovementCommands
                    )
                }
            }

            // Success/Error toast overlay
            (uiState.successMessage ?: uiState.errorMessage)?.let { message ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                ) {
                    MarsToast(
                        title =
                            if (uiState.successMessage != null) {
                                stringResource(R.string.toast_mission_success)
                            } else {
                                stringResource(R.string.toast_mission_failed)
                            },
                        message = message,
                        type =
                            if (uiState.successMessage != null) {
                                MarsToastType.Info
                            } else {
                                MarsToastType.Error
                            },
                        onClick = viewModel::clearMessages,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Auto-dismiss messages
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(
                        if (uiState.successMessage != null) SUCCESS_MESSAGE_DURATION_MS else ERROR_MESSAGE_DURATION_MS
                    )
                    viewModel.clearMessages()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("CyclomaticComplexMethod")
@Composable
internal fun NewMissionContent(
    uiState: NewMissionUiState,
    onInputModeChange: (InputMode) -> Unit,
    onJsonInputChange: (String) -> Unit,
    onPlateauWidthChange: (String) -> Unit,
    onPlateauHeightChange: (String) -> Unit,
    onRoverStartXChange: (String) -> Unit,
    onRoverStartYChange: (String) -> Unit,
    onRoverDirectionChange: (String) -> Unit,
    onMovementCommandsChange: (String) -> Unit,
    onExecuteMission: () -> Unit,
    onClearMessages: () -> Unit,
    onNavigateBack: () -> Unit,
    onPlateauWidthFocusLost: () -> Unit = {},
    onPlateauHeightFocusLost: () -> Unit = {},
    onRoverStartXFocusLost: () -> Unit = {},
    onRoverStartYFocusLost: () -> Unit = {},
    onMovementCommandsFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Input mode selector
        Text(
            text = stringResource(R.string.input_mode_selector_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = uiState.inputMode == InputMode.JSON,
                onClick = { onInputModeChange(InputMode.JSON) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text(stringResource(R.string.input_mode_json_short))
            }

            SegmentedButton(
                selected = uiState.inputMode == InputMode.INDIVIDUAL,
                onClick = { onInputModeChange(InputMode.INDIVIDUAL) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text(stringResource(R.string.input_mode_individual_short))
            }
        }

        // Input content based on mode
        when (uiState.inputMode) {
            InputMode.JSON -> {
                JsonInputSection(
                    jsonInput = uiState.jsonInput,
                    jsonError = uiState.jsonError,
                    onJsonInputChange = onJsonInputChange
                )
            }

            InputMode.INDIVIDUAL -> {
                IndividualInputsForm(
                    uiState = uiState,
                    onPlateauWidthChange = onPlateauWidthChange,
                    onPlateauHeightChange = onPlateauHeightChange,
                    onRoverStartXChange = onRoverStartXChange,
                    onRoverStartYChange = onRoverStartYChange,
                    onRoverDirectionChange = onRoverDirectionChange,
                    onMovementCommandsChange = onMovementCommandsChange,
                    onPlateauWidthFocusLost = onPlateauWidthFocusLost,
                    onPlateauHeightFocusLost = onPlateauHeightFocusLost,
                    onRoverStartXFocusLost = onRoverStartXFocusLost,
                    onRoverStartYFocusLost = onRoverStartYFocusLost,
                    onMovementCommandsFocusLost = onMovementCommandsFocusLost
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Execute button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsButton(
                text = stringResource(R.string.action_execute),
                onClick = onExecuteMission,
                variant = MarsButtonVariant.Primary,
                isLoading = uiState.isLoading,
                modifier = Modifier.weight(1f),
                contentDescription = "Execute mission with current parameters"
            )

            MarsButton(
                text = stringResource(R.string.action_cancel),
                onClick = onNavigateBack,
                variant = MarsButtonVariant.Secondary,
                enabled = !uiState.isLoading,
                contentDescription = "Cancel and return to previous screen"
            )
        }
    }
}

@Composable
private fun JsonInputSection(
    jsonInput: String,
    jsonError: String?,
    onJsonInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.json_configuration_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.json_configuration_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MarsTextField(
            value = jsonInput,
            onValueChange = onJsonInputChange,
            label = stringResource(R.string.input_json_label),
            errorMessage = jsonError,
            variant = MarsTextFieldVariant.Outlined,
            singleLine = false,
            maxLines = 10,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "New Mission - JSON Mode", showBackground = true)
@Composable
private fun NewMissionJsonPreview() {
    MarsRoverTheme {
        NewMissionContent(
            uiState = NewMissionUiState(inputMode = InputMode.JSON),
            onInputModeChange = { },
            onJsonInputChange = { },
            onPlateauWidthChange = { },
            onPlateauHeightChange = { },
            onRoverStartXChange = { },
            onRoverStartYChange = { },
            onRoverDirectionChange = { },
            onMovementCommandsChange = { },
            onExecuteMission = { },
            onClearMessages = { },
            onNavigateBack = {},
            onPlateauWidthFocusLost = {},
            onPlateauHeightFocusLost = {},
            onRoverStartXFocusLost = {},
            onRoverStartYFocusLost = {},
            onMovementCommandsFocusLost = {}
        )
    }
}

@Preview(name = "New Mission - Individual Mode", showBackground = true)
@Composable
private fun NewMissionIndividualPreview() {
    MarsRoverTheme {
        NewMissionContent(
            uiState = NewMissionUiState(inputMode = InputMode.INDIVIDUAL),
            onInputModeChange = { },
            onJsonInputChange = { },
            onPlateauWidthChange = { },
            onPlateauHeightChange = { },
            onRoverStartXChange = { },
            onRoverStartYChange = { },
            onRoverDirectionChange = { },
            onMovementCommandsChange = { },
            onExecuteMission = { },
            onClearMessages = { },
            onNavigateBack = {},
            onPlateauWidthFocusLost = {},
            onPlateauHeightFocusLost = {},
            onRoverStartXFocusLost = {},
            onRoverStartYFocusLost = {},
            onMovementCommandsFocusLost = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewMissionScreenPreview() {
    MarsRoverTheme {
        NewMissionContent(
            uiState =
                NewMissionUiState(
                    inputMode = InputMode.INDIVIDUAL,
                    plateauWidth = "5",
                    plateauHeight = "5",
                    roverStartX = "1",
                    roverStartY = "2",
                    roverStartDirection = "N",
                    movementCommands = "LMLMLMLMM",
                    errorMessage = "Invalid input format"
                ),
            onInputModeChange = {},
            onJsonInputChange = {},
            onPlateauWidthChange = {},
            onPlateauHeightChange = {},
            onRoverStartXChange = {},
            onRoverStartYChange = {},
            onRoverDirectionChange = {},
            onMovementCommandsChange = {},
            onExecuteMission = {},
            onClearMessages = {},
            onNavigateBack = {},
            onPlateauWidthFocusLost = {},
            onPlateauHeightFocusLost = {},
            onRoverStartXFocusLost = {},
            onRoverStartYFocusLost = {},
            onMovementCommandsFocusLost = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewMissionJsonScreenPreview() {
    MarsRoverTheme {
        NewMissionContent(
            uiState =
                NewMissionUiState(
                    inputMode = InputMode.JSON,
                    jsonInput =
                        """
                        {
                            "plateauWidth": 5,
                            "plateauHeight": 5,
                            "rovers": [
                                {
                                    "x": 1,
                                    "y": 2,
                                    "direction": "N",
                                    "commands": "LMLMLMLMM"
                                }
                            ]
                        }
                        """.trimIndent(),
                    jsonError = "Invalid JSON format"
                ),
            onInputModeChange = {},
            onJsonInputChange = {},
            onPlateauWidthChange = {},
            onPlateauHeightChange = {},
            onRoverStartXChange = {},
            onRoverStartYChange = {},
            onRoverDirectionChange = {},
            onMovementCommandsChange = {},
            onExecuteMission = {},
            onClearMessages = {},
            onNavigateBack = {},
            onPlateauWidthFocusLost = {},
            onPlateauHeightFocusLost = {},
            onRoverStartXFocusLost = {},
            onRoverStartYFocusLost = {},
            onMovementCommandsFocusLost = {}
        )
    }
}
