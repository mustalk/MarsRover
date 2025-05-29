@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.presentation.ui.components.MarsCard
import com.mustalk.seat.marsrover.presentation.ui.dashboard.MissionResult
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_INPUT_PREVIEW_LENGTH = 50
private const val EXAMPLE_MISSION_INPUT =
    """{"topRightCorner": {"x": 5, "y": 5}, "roverPosition": {"x": 1, "y": 2}, "roverDirection": "N", "movements": "LMLMLMLMM"}"""

/**
 * Card component that displays the result of a completed rover mission.
 * Shows final position, success status, and timestamp.
 */
@Composable
fun MissionResultCard(
    missionResult: MissionResult,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(missionResult.timestamp))

    MarsCard(
        title = stringResource(R.string.mission_result),
        modifier = modifier,
        contentDescription = stringResource(R.string.cd_mission_card)
    ) {
        // Status row with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector =
                    if (missionResult.isSuccess) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Warning
                    },
                contentDescription =
                    if (missionResult.isSuccess) {
                        "Mission successful"
                    } else {
                        "Mission failed"
                    },
                tint =
                    if (missionResult.isSuccess) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text =
                    if (missionResult.isSuccess) {
                        "Mission Completed Successfully"
                    } else {
                        "Mission Failed"
                    },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (missionResult.isSuccess) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Final position
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.rover_final_position, missionResult.finalPosition),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Completed on $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Show original input if available
            missionResult.originalInput?.let { input ->
                Spacer(modifier = Modifier.height(8.dp))
                val truncatedInput = input.take(MAX_INPUT_PREVIEW_LENGTH)
                val suffix = if (input.length > MAX_INPUT_PREVIEW_LENGTH) "..." else ""
                Text(
                    text = "Input: $truncatedInput$suffix",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Mission Result Card - Success", showBackground = true)
@Composable
private fun MissionResultCardSuccessPreview() {
    MarsRoverTheme {
        MissionResultCard(
            missionResult =
                MissionResult(
                    finalPosition = "1 3 N",
                    isSuccess = true,
                    originalInput = EXAMPLE_MISSION_INPUT
                ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Mission Result Card - Failed", showBackground = true)
@Composable
private fun MissionResultCardFailedPreview() {
    MarsRoverTheme {
        MissionResultCard(
            missionResult =
                MissionResult(
                    finalPosition = "Error: Invalid JSON",
                    isSuccess = false,
                    originalInput = """{"invalid": "json"}"""
                ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
