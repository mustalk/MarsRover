package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Mars-themed text field component with validation and error handling.
 * Follows Mars Rover design system with consistent styling.
 */
@Composable
fun MarsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    variant: MarsTextFieldVariant = MarsTextFieldVariant.Outlined,
    contentDescription: String? = null,
    onFocusChanged: ((FocusState) -> Unit)? = null,
) {
    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier
        }

    val focusModifier =
        if (onFocusChanged != null) {
            Modifier.onFocusChanged(onFocusChanged)
        } else {
            Modifier
        }

    Column(modifier = semanticsModifier) {
        when (variant) {
            MarsTextFieldVariant.Filled -> {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label) },
                    placeholder =
                        placeholder?.let {
                            {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        },
                    isError = isError,
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    colors =
                        TextFieldDefaults.colors(
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(focusModifier)
                )
            }
            MarsTextFieldVariant.Outlined -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label) },
                    placeholder =
                        placeholder?.let {
                            {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        },
                    isError = isError,
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(focusModifier)
                )
            }
        }

        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Mars-themed text field component with TextFieldValue for cursor control.
 */
@Composable
fun MarsTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    variant: MarsTextFieldVariant = MarsTextFieldVariant.Outlined,
    contentDescription: String? = null,
    onFocusChanged: ((FocusState) -> Unit)? = null,
) {
    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier
        }

    val focusModifier =
        if (onFocusChanged != null) {
            Modifier.onFocusChanged(onFocusChanged)
        } else {
            Modifier
        }

    Column(modifier = semanticsModifier) {
        when (variant) {
            MarsTextFieldVariant.Filled -> {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label) },
                    placeholder =
                        placeholder?.let {
                            {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        },
                    isError = isError,
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    colors =
                        TextFieldDefaults.colors(
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(focusModifier)
                )
            }

            MarsTextFieldVariant.Outlined -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label) },
                    placeholder =
                        placeholder?.let {
                            {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        },
                    isError = isError,
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(focusModifier)
                )
            }
        }

        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Available text field variants following Mars Rover design system
 */
enum class MarsTextFieldVariant {
    Filled, // Filled text field with background
    Outlined, // Outlined text field with border
}

@Preview(name = "Mars Text Fields - Light", showBackground = true)
@Composable
private fun MarsTextFieldPreviewLight() {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("Invalid input") }
    var text3 by remember { mutableStateOf("1 2 N") }

    MarsRoverTheme(darkTheme = false) {
        Column(modifier = Modifier.padding(16.dp)) {
            MarsTextField(
                value = text1,
                onValueChange = { text1 = it },
                label = "JSON Mission Data",
                placeholder = "Enter mission JSON...",
                variant = MarsTextFieldVariant.Outlined
            )

            MarsTextField(
                value = text2,
                onValueChange = { text2 = it },
                label = "Rover Position",
                errorMessage = "Invalid position format",
                variant = MarsTextFieldVariant.Outlined,
                modifier = Modifier.padding(top = 16.dp)
            )

            MarsTextField(
                value = text3,
                onValueChange = { text3 = it },
                label = "Mission Result",
                readOnly = true,
                variant = MarsTextFieldVariant.Filled,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(name = "Mars Text Fields - Dark", showBackground = true)
@Composable
private fun MarsTextFieldPreviewDark() {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("5") }

    MarsRoverTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            MarsTextField(
                value = text1,
                onValueChange = { text1 = it },
                label = "Movement Commands",
                placeholder = "LMLMLMLMM",
                variant = MarsTextFieldVariant.Outlined
            )

            MarsTextField(
                value = text2,
                onValueChange = { text2 = it },
                label = "Plateau Width",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                variant = MarsTextFieldVariant.Filled,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
