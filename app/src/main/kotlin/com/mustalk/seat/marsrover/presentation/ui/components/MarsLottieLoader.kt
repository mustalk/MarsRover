package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Mars-themed loading indicator that uses Lottie animation.
 *
 * Displays an animated Mars rover with loading text for network operations.
 */
@Composable
fun MarsLottieLoader(
    message: String = stringResource(R.string.loading_mission_execution),
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
) {
    // Load the Lottie composition from assets
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("mars-rover-lottie.json")
    )

    // Animate the composition
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    AnimatedVisibility(
        visible = isVisible,
        enter =
            fadeIn(
                animationSpec = tween(Constants.UI.LOADING_ANIMATION_DURATION_MS.toInt())
            ),
        exit =
            fadeOut(
                animationSpec = tween(Constants.UI.LOADING_ANIMATION_DURATION_MS.toInt())
            ),
        modifier = modifier
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ),
            contentAlignment = Alignment.Center
        ) {
            MarsCard(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Lottie Animation
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(Constants.UI.LOTTIE_ANIMATION_SIZE_DP.dp)
                    )

                    // Loading text
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Secondary text
                    Text(
                        text = stringResource(R.string.loading_please_wait),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Compact version of the Mars Lottie loader for inline use.
 */
@Composable
fun MarsLottieLoaderCompact(
    message: String = stringResource(R.string.loading_mission_execution),
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
) {
    // Load the Lottie composition from assets
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("mars-rover-lottie.json")
    )

    // Animate the composition
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    AnimatedVisibility(
        visible = isVisible,
        enter =
            fadeIn(
                animationSpec = tween(Constants.UI.LOADING_ANIMATION_DURATION_MS.toInt())
            ),
        exit =
            fadeOut(
                animationSpec = tween(Constants.UI.LOADING_ANIMATION_DURATION_MS.toInt())
            ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Lottie Animation
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(Constants.UI.LOTTIE_ANIMATION_SIZE_COMPACT_DP.dp)
            )

            // Loading text
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
 * Full-screen Mars Lottie loader overlay.
 */
@Composable
fun MarsFullScreenLottieLoader(message: String = stringResource(R.string.loading_mission_execution)) {
    MarsLottieLoader(
        message = message,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(name = "Mars Lottie Loader", showBackground = true)
@Composable
private fun MarsLottieLoaderPreview() {
    MarsRoverTheme {
        MarsLottieLoader(
            message = "Executing Mars rover mission...",
            modifier = Modifier.size(300.dp)
        )
    }
}

@Preview(name = "Mars Lottie Loader Compact", showBackground = true)
@Composable
private fun MarsLottieLoaderCompactPreview() {
    MarsRoverTheme {
        MarsLottieLoaderCompact(
            message = "Loading..."
        )
    }
}

@Preview(name = "Mars Full Screen Lottie Loader", showBackground = true)
@Composable
private fun MarsFullScreenLottieLoaderPreview() {
    MarsRoverTheme {
        MarsFullScreenLottieLoader(
            message = "Processing mission commands..."
        )
    }
}
