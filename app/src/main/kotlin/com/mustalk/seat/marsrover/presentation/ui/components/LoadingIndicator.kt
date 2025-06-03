package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Mars-themed loading indicator component with customizable size and message.
 * Follows Mars Rover design system with consistent styling and accessibility.
 */
@Composable
fun MarsLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.mission_loading),
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    showMessage: Boolean = true,
    contentDescription: String? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = Constants.UI.LOADING_ANIMATION_DURATION_MS.toInt())
            ),
        label = "rotation_animation"
    )

    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier.semantics { this.contentDescription = message }
        }

    Column(
        modifier = semanticsModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .size(size)
                    .rotate(rotation),
            strokeWidth = strokeWidth,
            color = color,
            strokeCap = StrokeCap.Round
        )

        if (showMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Full-screen Mars loading indicator for major operations
 */
@Composable
fun MarsFullScreenLoader(
    message: String = stringResource(R.string.mission_loading),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MarsLoadingIndicator(
            message = message,
            size = 64.dp,
            strokeWidth = 5.dp
        )
    }
}

@Preview(name = "Mars Loading Indicators - Light", showBackground = true)
@Composable
private fun MarsLoadingIndicatorPreviewLight() {
    MarsRoverTheme(darkTheme = false) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            MarsLoadingIndicator(
                message = "Processing mission data...",
                size = 32.dp
            )

            MarsLoadingIndicator(
                message = "Executing rover commands...",
                size = 48.dp
            )

            MarsLoadingIndicator(
                message = "Establishing Mars connection...",
                size = 64.dp,
                strokeWidth = 6.dp
            )
        }
    }
}

@Preview(name = "Mars Loading Indicators - Dark", showBackground = true)
@Composable
private fun MarsLoadingIndicatorPreviewDark() {
    MarsRoverTheme(darkTheme = true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            MarsLoadingIndicator(
                message = "Validating plateau bounds...",
                showMessage = false,
                size = 40.dp
            )

            MarsFullScreenLoader(
                message = "Mission in progress..."
            )
        }
    }
}
