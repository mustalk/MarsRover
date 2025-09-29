package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme

/**
 * Mars-themed button component with loading states and different variants.
 * Follows Mars Rover design system with consistent styling.
 */
@Composable
fun MarsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MarsButtonVariant = MarsButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val buttonColors =
        when (variant) {
            MarsButtonVariant.Primary -> ButtonDefaults.buttonColors()
            MarsButtonVariant.Secondary ->
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            MarsButtonVariant.Text -> ButtonDefaults.textButtonColors()
        }

    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier
        }

    when (variant) {
        MarsButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = semanticsModifier,
                enabled = enabled && !isLoading,
                colors = buttonColors,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                MarsButtonContent(text = text, isLoading = isLoading)
            }
        }

        MarsButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = semanticsModifier,
                enabled = enabled && !isLoading,
                colors = buttonColors,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                MarsButtonContent(text = text, isLoading = isLoading)
            }
        }

        MarsButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                modifier = semanticsModifier,
                enabled = enabled && !isLoading,
                colors = buttonColors,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                MarsButtonContent(text = text, isLoading = isLoading)
            }
        }
    }
}

@Composable
private fun MarsButtonContent(
    text: String,
    isLoading: Boolean,
) {
    if (isLoading) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Available button variants following Mars Rover design system
 */
enum class MarsButtonVariant {
    Primary, // Filled button with primary color
    Secondary, // Outlined button
    Text, // Text-only button
}

@Preview(name = "Mars Buttons - Light", showBackground = true)
@Composable
private fun MarsButtonPreviewLight() {
    MarsRoverTheme(darkTheme = false) {
        Box(modifier = Modifier) {
            Row {
                MarsButton(
                    text = "Execute Mission",
                    onClick = { },
                    variant = MarsButtonVariant.Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                MarsButton(
                    text = "Cancel",
                    onClick = { },
                    variant = MarsButtonVariant.Secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                MarsButton(
                    text = "Skip",
                    onClick = { },
                    variant = MarsButtonVariant.Text
                )
            }
        }
    }
}

@Preview(name = "Mars Buttons - Dark", showBackground = true)
@Composable
private fun MarsButtonPreviewDark() {
    MarsRoverTheme(darkTheme = true) {
        Box(modifier = Modifier) {
            Row {
                MarsButton(
                    text = "Execute Mission",
                    onClick = { },
                    variant = MarsButtonVariant.Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                MarsButton(
                    text = "Loading...",
                    onClick = { },
                    variant = MarsButtonVariant.Secondary,
                    isLoading = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                MarsButton(
                    text = "Disabled",
                    onClick = { },
                    variant = MarsButtonVariant.Text,
                    enabled = false
                )
            }
        }
    }
}
