package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Mars-themed card component for consistent container styling.
 * Follows Mars Rover design system with consistent styling and accessibility.
 */
@Composable
fun MarsCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Dp = 0.dp,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val semanticsModifier =
        if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier
        }

    Card(
        modifier = semanticsModifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }

            content()
        }
    }
}

/**
 * Mars-themed card with primary container colors for highlighting important content
 */
@Composable
fun MarsPrimaryCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    MarsCard(
        modifier = modifier,
        title = title,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = 10.dp,
        contentDescription = contentDescription,
        content = content
    )
}

/**
 * Mars-themed card with surface variant colors for secondary content
 */
@Composable
fun MarsSecondaryCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    MarsCard(
        modifier = modifier,
        title = title,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        elevation = 6.dp,
        contentDescription = contentDescription,
        content = content
    )
}

@Preview(name = "Mars Cards - Light", showBackground = true)
@Composable
private fun MarsCardPreviewLight() {
    MarsRoverTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsCard(
                title = "Mission Results"
            ) {
                Text(
                    text = "Final Position: 1 3 N",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Mission completed successfully",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            MarsPrimaryCard(
                title = "Active Mission"
            ) {
                Text(
                    text = "Rover is currently executing movement commands",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Commands: LMLMLMLMM",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            MarsSecondaryCard(
                title = "Mission History"
            ) {
                Text(
                    text = "Last 3 missions completed",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "View detailed mission logs",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(name = "Mars Cards - Dark", showBackground = true)
@Composable
private fun MarsCardPreviewDark() {
    MarsRoverTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MarsCard {
                Text(
                    text = "No title card example",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "This card demonstrates content without a title",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            MarsPrimaryCard(
                title = "Plateau Configuration"
            ) {
                Text(
                    text = "Size: 5x5 grid",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Surface type: Rocky terrain",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
