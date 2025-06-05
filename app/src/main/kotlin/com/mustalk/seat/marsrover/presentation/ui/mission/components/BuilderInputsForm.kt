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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.core.ui.components.MarsButton
import com.mustalk.seat.marsrover.core.ui.components.MarsButtonVariant
import com.mustalk.seat.marsrover.core.ui.components.MarsCard
import com.mustalk.seat.marsrover.core.ui.components.MarsTextField
import com.mustalk.seat.marsrover.core.ui.components.MarsTextFieldVariant
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import com.mustalk.seat.marsrover.presentation.ui.mission.NewMissionUiState

/**
 * Form component for builder mission parameter inputs.
 * Organizes inputs by logical groups: plateau, rover position, and commands.
 */
@Composable
fun BuilderInputsForm(
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            text = stringResource(R.string.plateau_configuration_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            text = stringResource(R.string.rover_start_position_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Position coordinates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MarsTextField(
                value = startX,
                onValueChange = onStartXChange,
                label = stringResource(R.string.rover_start_x),
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
                label = stringResource(R.string.rover_start_y),
                errorMessage = startYError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Outlined,
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) onStartYFocusLost()
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Direction selection
        Text(
            text = stringResource(R.string.rover_direction),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

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
            horizontalArrangement = Arrangement.SpaceEvenly
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
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text =
                            when (direction) {
                                "N" -> stringResource(R.string.direction_north)
                                "E" -> stringResource(R.string.direction_east)
                                else -> direction
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Second row: South and West
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text =
                            when (direction) {
                                "S" -> stringResource(R.string.direction_south)
                                "W" -> stringResource(R.string.direction_west)
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
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(commands)) }

    // Sync TextFieldValue with commands when commands change externally
    if (textFieldValue.text != commands) {
        textFieldValue =
            TextFieldValue(
                text = commands,
                selection = TextRange(commands.length)
            )
    }

    MarsCard(
        title = stringResource(R.string.rover_movements),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.movement_commands_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                Triple("L", stringResource(R.string.action_turn_left), stringResource(R.string.cd_add_left_turn)),
                Triple("R", stringResource(R.string.action_turn_right), stringResource(R.string.cd_add_right_turn)),
                Triple("M", stringResource(R.string.action_move_forward), stringResource(R.string.cd_add_move_forward))
            ).forEach { (command, label, contentDesc) ->
                MarsButton(
                    text = command,
                    onClick = {
                        val newCommands = commands + command
                        textFieldValue =
                            TextFieldValue(
                                text = newCommands,
                                selection = TextRange(newCommands.length)
                            )
                        onCommandsChange(newCommands)
                        focusManager.clearFocus()
                    },
                    variant = MarsButtonVariant.Primary,
                    contentDescription = contentDesc,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        MarsTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onCommandsChange(it.text)
            },
            label = stringResource(R.string.movement_commands_label),
            placeholder = stringResource(R.string.movement_commands_placeholder),
            errorMessage = commandsError,
            variant = MarsTextFieldVariant.Outlined,
            onFocusChanged = { focusState ->
                if (!focusState.isFocused) onCommandsFocusLost()
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        MarsButton(
            text = stringResource(R.string.action_clear),
            onClick = {
                textFieldValue = TextFieldValue("")
                onCommandsChange("")
                focusManager.clearFocus()
            },
            variant = MarsButtonVariant.Secondary,
            contentDescription = stringResource(R.string.cd_clear_commands),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Builder Inputs Form", showBackground = true)
@Composable
private fun BuilderInputsFormPreview() {
    MarsRoverTheme {
        BuilderInputsForm(
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
