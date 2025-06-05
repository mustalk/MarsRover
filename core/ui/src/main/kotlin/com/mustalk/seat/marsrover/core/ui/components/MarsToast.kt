package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.core.ui.R
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme

/**
 * Mars-themed toast component for displaying error, warning, and info messages.
 * Follows Mars Rover design system with consistent styling and accessibility.
 */
@Composable
fun MarsToast(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    type: MarsToastType = MarsToastType.Error,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val (backgroundColor, contentColor, icon) =
        when (type) {
            MarsToastType.Error ->
                Triple(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer,
                    Icons.Filled.Warning
                )

            MarsToastType.Warning ->
                Triple(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer,
                    Icons.Filled.Warning
                )

            MarsToastType.Info ->
                Triple(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    Icons.Filled.Info
                )
        }

    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            val defaultContentDescription =
                when (type) {
                    MarsToastType.Error -> stringResource(R.string.cd_error_toast)
                    MarsToastType.Warning -> stringResource(R.string.cd_warning_toast)
                    MarsToastType.Info -> stringResource(R.string.cd_info_toast)
                }
            modifier.semantics { this.contentDescription = defaultContentDescription }
        }

    val clickableModifier =
        if (onClick != null) {
            semanticsModifier.clickable { onClick() }
        } else {
            semanticsModifier
        }

    Card(
        modifier = clickableModifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription =
                    when (type) {
                        MarsToastType.Error -> stringResource(R.string.cd_error_icon)
                        MarsToastType.Warning -> stringResource(R.string.cd_warning_icon)
                        MarsToastType.Info -> stringResource(R.string.cd_info_icon)
                    },
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Available toast types following Mars Rover design system
 */
enum class MarsToastType {
    Error, // Critical errors (red)
    Warning, // Warnings (orange/yellow)
    Info, // Information (blue)
}

@Preview(name = "Mars Toasts - Light", showBackground = true)
@Composable
private fun MarsToastPreviewLight() {
    MarsRoverTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsToast(
                title = "Mission Failed",
                message = "Invalid JSON format detected in mission data",
                type = MarsToastType.Error
            )

            MarsToast(
                title = "Boundary Warning",
                message = "Rover attempted to move outside plateau bounds",
                type = MarsToastType.Warning
            )

            MarsToast(
                message = "Mission completed successfully. Final position: 1 3 N",
                type = MarsToastType.Info
            )
        }
    }
}

@Preview(name = "Mars Toasts - Dark", showBackground = true)
@Composable
private fun MarsToastPreviewDark() {
    MarsRoverTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsToast(
                title = "Connection Error",
                message = "Unable to establish communication with Mars rover",
                type = MarsToastType.Error
            )

            MarsToast(
                message = "Processing mission commands...",
                type = MarsToastType.Info
            )
        }
    }
}
