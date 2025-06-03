package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.presentation.ui.components.MarsButton
import com.mustalk.seat.marsrover.presentation.ui.components.MarsButtonVariant
import com.mustalk.seat.marsrover.presentation.ui.components.MarsCard
import com.mustalk.seat.marsrover.presentation.ui.components.MarsLottieLoader
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextField
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextFieldVariant
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToast
import com.mustalk.seat.marsrover.presentation.ui.components.MarsToastType
import com.mustalk.seat.marsrover.presentation.ui.mission.components.BuilderInputsForm
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsTopAppBarDark
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsTopAppBarLight
import kotlinx.coroutines.delay

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

    // Handle mission completion navigation
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null && !uiState.isLoading) {
            // Extract final position from success message
            val finalPosition =
                uiState.successMessage
                    ?.substringAfter("Final position: ")
                    ?.substringBefore(",") ?: ""

            val originalInput =
                when (uiState.inputMode) {
                    InputMode.JSON -> uiState.jsonInput
                    InputMode.BUILDER ->
                        """
                        {
                            "topRightCorner": {
                                "x": ${uiState.plateauWidth},
                                "y": ${uiState.plateauHeight}
                            },
                            "roverPosition": {
                                "x": ${uiState.roverStartX},
                                "y": ${uiState.roverStartY}
                            },
                            "roverDirection": "${uiState.roverStartDirection}",
                            "movements": "${uiState.movementCommands}"
                        }
                        """.trimIndent()
                }

            onMissionCompleted(finalPosition, true, originalInput)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            if (isSystemInDarkTheme()) {
                                MarsTopAppBarDark
                            } else {
                                MarsTopAppBarLight
                            },
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
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
            // Mars background image
            Image(
                painter = painterResource(id = R.drawable.mars_background),
                contentDescription = stringResource(R.string.cd_mars_background),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when {
                uiState.isLoading -> {
                    MarsLottieLoader(
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
                        onNavigateBack = onNavigateBack,
                        onPlateauWidthFocusLost = viewModel::validatePlateauWidth,
                        onPlateauHeightFocusLost = viewModel::validatePlateauHeight,
                        onRoverStartXFocusLost = viewModel::validateRoverStartX,
                        onRoverStartYFocusLost = viewModel::validateRoverStartY,
                        onMovementCommandsFocusLost = viewModel::validateMovementCommands
                    )
                }
            }

            // Error toast overlay (only show error messages)
            uiState.errorMessage?.let { message ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                ) {
                    MarsToast(
                        title = stringResource(R.string.toast_mission_failed),
                        message = message,
                        type = MarsToastType.Error,
                        onClick = viewModel::clearMessages,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Auto-dismiss error messages
                LaunchedEffect(message) {
                    delay(Constants.UI.ERROR_MESSAGE_DURATION_MS)
                    viewModel.clearMessages()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        InputModeSelector(
            selectedMode = uiState.inputMode,
            onModeChange = onInputModeChange
        )

        InputContentSection(
            uiState = uiState,
            onJsonInputChange = onJsonInputChange,
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

        Spacer(modifier = Modifier.height(8.dp))

        ActionButtonsSection(
            uiState = uiState,
            onExecuteMission = onExecuteMission,
            onNavigateBack = onNavigateBack
        )
    }
}

/**
 * Segmented control for switching between JSON input and form builder input modes.
 *
 * @param selectedMode Currently selected input mode
 * @param onModeChange Callback when input mode changes
 * @param modifier Optional modifier for styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputModeSelector(
    selectedMode: InputMode,
    onModeChange: (InputMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.input_mode_selector_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedMode == InputMode.JSON,
                onClick = { onModeChange(InputMode.JSON) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.surface,
                        activeContentColor = MaterialTheme.colorScheme.onSurface,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = Constants.UI.ALPHA_HALF),
                        inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = Constants.UI.ALPHA_INACTIVE_TEXT)
                    )
            ) {
                Text(stringResource(R.string.input_mode_json_short))
            }

            SegmentedButton(
                selected = selectedMode == InputMode.BUILDER,
                onClick = { onModeChange(InputMode.BUILDER) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.surface,
                        activeContentColor = MaterialTheme.colorScheme.onSurface,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = Constants.UI.ALPHA_HALF),
                        inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = Constants.UI.ALPHA_INACTIVE_TEXT)
                    )
            ) {
                Text(stringResource(R.string.input_mode_builder_short))
            }
        }
    }
}

/**
 * Renders the appropriate input content based on the selected input mode.
 * Switches between JSON text input and form builder UI.
 *
 * @param uiState Current UI state containing input mode and values
 * @param onJsonInputChange Callback for JSON text changes
 * @param modifier Optional modifier for styling
 */
@Composable
private fun InputContentSection(
    uiState: NewMissionUiState,
    onJsonInputChange: (String) -> Unit,
    onPlateauWidthChange: (String) -> Unit,
    onPlateauHeightChange: (String) -> Unit,
    onRoverStartXChange: (String) -> Unit,
    onRoverStartYChange: (String) -> Unit,
    onRoverDirectionChange: (String) -> Unit,
    onMovementCommandsChange: (String) -> Unit,
    onPlateauWidthFocusLost: () -> Unit,
    onPlateauHeightFocusLost: () -> Unit,
    onRoverStartXFocusLost: () -> Unit,
    onRoverStartYFocusLost: () -> Unit,
    onMovementCommandsFocusLost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.inputMode) {
        InputMode.JSON -> {
            JsonInputSection(
                jsonInput = uiState.jsonInput,
                jsonError = uiState.jsonError,
                onJsonInputChange = onJsonInputChange,
                modifier = modifier
            )
        }

        InputMode.BUILDER -> {
            BuilderInputsForm(
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
                onMovementCommandsFocusLost = onMovementCommandsFocusLost,
                modifier = modifier
            )
        }
    }
}

/**
 * Action buttons section with Execute and Cancel buttons.
 * Execute button shows loading state during mission execution.
 *
 * @param uiState Current UI state for loading and button states
 * @param onExecuteMission Callback to start mission execution
 * @param onNavigateBack Callback to cancel and navigate back
 * @param modifier Optional modifier for styling
 */
@Composable
private fun ActionButtonsSection(
    uiState: NewMissionUiState,
    onExecuteMission: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MarsButton(
            text = stringResource(R.string.action_execute),
            onClick = onExecuteMission,
            variant = MarsButtonVariant.Primary,
            isLoading = uiState.isLoading,
            modifier = Modifier.weight(1f),
            contentDescription = stringResource(R.string.cd_execute_mission)
        )

        MarsButton(
            text = stringResource(R.string.action_cancel),
            onClick = onNavigateBack,
            variant = MarsButtonVariant.Secondary,
            enabled = !uiState.isLoading,
            contentDescription = stringResource(R.string.cd_cancel_mission)
        )
    }
}

@Composable
private fun JsonInputSection(
    jsonInput: String,
    jsonError: String?,
    onJsonInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    MarsCard(
        title = stringResource(R.string.json_configuration_title),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.json_configuration_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            uiState = NewMissionUiState(inputMode = InputMode.BUILDER),
            onInputModeChange = { },
            onJsonInputChange = { },
            onPlateauWidthChange = { },
            onPlateauHeightChange = { },
            onRoverStartXChange = { },
            onRoverStartYChange = { },
            onRoverDirectionChange = { },
            onMovementCommandsChange = { },
            onExecuteMission = { },
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
                    inputMode = InputMode.BUILDER,
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
            onNavigateBack = {},
            onPlateauWidthFocusLost = {},
            onPlateauHeightFocusLost = {},
            onRoverStartXFocusLost = {},
            onRoverStartYFocusLost = {},
            onMovementCommandsFocusLost = {}
        )
    }
}
