package com.mustalk.seat.marsrover.presentation.ui.mission.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.presentation.ui.components.MarsCard
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextField
import com.mustalk.seat.marsrover.presentation.ui.components.MarsTextFieldVariant
import com.mustalk.seat.marsrover.presentation.ui.mission.NewMissionUiState
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Form component for individual mission parameter inputs.
 * Organizes inputs by logical groups: plateau, rover position, and commands.
 */
@Composable
fun IndividualInputsForm(
    uiState: NewMissionUiState,
    onPlateauWidthChange: (String) -> Unit,
    onPlateauHeightChange: (String) -> Unit,
    onRoverStartXChange: (String) -> Unit,
    onRoverStartYChange: (String) -> Unit,
    onRoverDirectionChange: (String) -> Unit,
    onMovementCommandsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onPlateauWidthFocusLost: () -> Unit = {},
    onPlateauHeightFocusLost: () -> Unit = {},
    onRoverStartXFocusLost: () -> Unit = {},
    onRoverStartYFocusLost: () -> Unit = {},
    onMovementCommandsFocusLost: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Plateau Configuration
        PlateauConfigSection(
            width = uiState.plateauWidth,
            height = uiState.plateauHeight,
            widthError = uiState.plateauWidthError,
            heightError = uiState.plateauHeightError,
            onWidthChange = onPlateauWidthChange,
            onHeightChange = onPlateauHeightChange,
            onWidthFocusLost = onPlateauWidthFocusLost,
            onHeightFocusLost = onPlateauHeightFocusLost
        )

        // Rover Initial Position
        RoverPositionSection(
            startX = uiState.roverStartX,
            startY = uiState.roverStartY,
            direction = uiState.roverStartDirection,
            startXError = uiState.roverStartXError,
            startYError = uiState.roverStartYError,
            directionError = uiState.roverStartDirectionError,
            onStartXChange = onRoverStartXChange,
            onStartYChange = onRoverStartYChange,
            onDirectionChange = onRoverDirectionChange,
            onStartXFocusLost = onRoverStartXFocusLost,
            onStartYFocusLost = onRoverStartYFocusLost
        )

        // Movement Commands
        MovementCommandsSection(
            commands = uiState.movementCommands,
            commandsError = uiState.movementCommandsError,
            onCommandsChange = onMovementCommandsChange,
            onCommandsFocusLost = onMovementCommandsFocusLost
        )
    }
}

@Composable
private fun PlateauConfigSection(
    width: String,
    height: String,
    widthError: String?,
    heightError: String?,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWidthFocusLost: () -> Unit = {},
    onHeightFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    MarsCard(
        title = stringResource(R.string.plateau_size),
        modifier = modifier
    ) {
        Text(
            text = "Define the exploration area dimensions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsTextField(
                value = width,
                onValueChange = onWidthChange,
                label = stringResource(R.string.plateau_width),
                errorMessage = widthError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Outlined,
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) onWidthFocusLost()
                },
                modifier = Modifier.weight(1f)
            )

            MarsTextField(
                value = height,
                onValueChange = onHeightChange,
                label = stringResource(R.string.plateau_height),
                errorMessage = heightError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Outlined,
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) onHeightFocusLost()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoverPositionSection(
    startX: String,
    startY: String,
    direction: String,
    startXError: String?,
    startYError: String?,
    directionError: String?,
    onStartXChange: (String) -> Unit,
    onStartYChange: (String) -> Unit,
    onDirectionChange: (String) -> Unit,
    onStartXFocusLost: () -> Unit = {},
    onStartYFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    MarsCard(
        title = stringResource(R.string.rover_position),
        modifier = modifier
    ) {
        Text(
            text = "Set the rover's starting position and orientation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Position coordinates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsTextField(
                value = startX,
                onValueChange = onStartXChange,
                label = "Start X",
                errorMessage = startXError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Outlined,
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) onStartXFocusLost()
                },
                modifier = Modifier.weight(1f)
            )

            MarsTextField(
                value = startY,
                onValueChange = onStartYChange,
                label = "Start Y",
                errorMessage = startYError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Outlined,
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) onStartYFocusLost()
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direction selection
        Text(
            text = stringResource(R.string.rover_direction),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        DirectionSelector(
            selectedDirection = direction,
            onDirectionSelected = onDirectionChange,
            error = directionError
        )
    }
}

@Composable
private fun DirectionSelector(
    selectedDirection: String,
    onDirectionSelected: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // First row: North and East
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("N", "E").forEach { direction ->
                Row(
                    modifier =
                        Modifier
                            .selectable(
                                selected = selectedDirection == direction,
                                onClick = { onDirectionSelected(direction) },
                                role = Role.RadioButton
                            ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedDirection == direction,
                        onClick = { onDirectionSelected(direction) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text =
                            when (direction) {
                                "N" -> "North"
                                "E" -> "East"
                                else -> direction
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second row: South and West
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("S", "W").forEach { direction ->
                Row(
                    modifier =
                        Modifier
                            .selectable(
                                selected = selectedDirection == direction,
                                onClick = { onDirectionSelected(direction) },
                                role = Role.RadioButton
                            ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedDirection == direction,
                        onClick = { onDirectionSelected(direction) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text =
                            when (direction) {
                                "S" -> "South"
                                "W" -> "West"
                                else -> direction
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun MovementCommandsSection(
    commands: String,
    commandsError: String?,
    onCommandsChange: (String) -> Unit,
    onCommandsFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    MarsCard(
        title = stringResource(R.string.rover_movements),
        modifier = modifier
    ) {
        Text(
            text = "Enter movement commands (L = Left, R = Right, M = Move)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        MarsTextField(
            value = commands,
            onValueChange = onCommandsChange,
            label = "Movement Commands",
            placeholder = "LMLMLMLMM",
            errorMessage = commandsError,
            variant = MarsTextFieldVariant.Outlined,
            onFocusChanged = { focusState ->
                if (!focusState.isFocused) onCommandsFocusLost()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Individual Inputs Form", showBackground = true)
@Composable
private fun IndividualInputsFormPreview() {
    MarsRoverTheme {
        IndividualInputsForm(
            uiState = NewMissionUiState(),
            onPlateauWidthChange = { },
            onPlateauHeightChange = { },
            onRoverStartXChange = { },
            onRoverStartYChange = { },
            onRoverDirectionChange = { },
            onMovementCommandsChange = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}
