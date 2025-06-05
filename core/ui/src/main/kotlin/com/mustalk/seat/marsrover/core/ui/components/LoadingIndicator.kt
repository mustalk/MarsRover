package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mustalk.seat.marsrover.core.ui.R
import com.mustalk.seat.marsrover.core.ui.constants.UiConstants
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme

/**
 * Mars-themed loading indicator component with customizable size and message.
 */
@Composable
fun MarsLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_mission_process),
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
                animation = tween(durationMillis = UiConstants.Animation.LOADING_DURATION_MS.toInt())
            ),
        label = "rotation_animation"
    )

    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            val defaultContentDescription = stringResource(R.string.cd_loading)
            modifier.semantics { this.contentDescription = defaultContentDescription }
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
            strokeCap = StrokeCap.Round,
            trackColor = color.copy(alpha = 0.3f)
        )

        if (showMessage) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Full-screen loading overlay with Mars theme.
 */
@Composable
fun MarsFullScreenLoader(
    message: String = stringResource(R.string.loading_mission_process),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = UiConstants.Layout.ALPHA_HALF)
                ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(UiConstants.Sizing.CARD_CORNER_RADIUS_DP.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            MarsLoadingIndicator(
                message = message,
                showMessage = true
            )
        }
    }
}

@Preview(name = "Mars Loading Indicators - Light", showBackground = true)
@Composable
private fun MarsLoadingIndicatorPreviewLight() {
    MarsRoverTheme(darkTheme = false) {
        MarsLoadingIndicator()
    }
}

@Preview(name = "Mars Loading Indicators - Dark", showBackground = true)
@Composable
private fun MarsFullScreenLoaderPreview() {
    MarsRoverTheme(darkTheme = true) {
        MarsFullScreenLoader()
    }
}
