@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.dashboard.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.core.utils.Constants
import com.mustalk.seat.marsrover.presentation.ui.components.MarsCard
import com.mustalk.seat.marsrover.presentation.ui.dashboard.MissionResult
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card component that displays the result of a completed rover mission.
 * Shows final position, success status, and timestamp with expandable original input.
 */
@Suppress("CyclomaticComplexMethod")
@Composable
fun MissionResultCard(
    missionResult: MissionResult,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(missionResult.timestamp))
    var isInputExpanded by remember { mutableStateOf(false) }

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
                        stringResource(R.string.cd_mission_success)
                    } else {
                        stringResource(R.string.cd_mission_failed)
                    },
                tint =
                    if (missionResult.isSuccess) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text =
                    if (missionResult.isSuccess) {
                        stringResource(R.string.mission_completed_successfully)
                    } else {
                        stringResource(R.string.mission_failed)
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

        Spacer(modifier = Modifier.height(6.dp))

        // Final position
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.rover_final_position, missionResult.finalPosition),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Show original input if available
            missionResult.originalInput?.let { input ->
                Spacer(modifier = Modifier.height(8.dp))

                // Input label with expand/collapse button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mission_instructions),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (input.length > Constants.UI.MAX_INPUT_PREVIEW_LENGTH) {
                        IconButton(
                            onClick = { isInputExpanded = !isInputExpanded },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isInputExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription =
                                    if (isInputExpanded) {
                                        stringResource(
                                            R.string.cd_collapse_input
                                        )
                                    } else {
                                        stringResource(R.string.cd_expand_input)
                                    },
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Input text - expandable
                Text(
                    text =
                        if (isInputExpanded || input.length <= Constants.UI.MAX_INPUT_PREVIEW_LENGTH) {
                            input
                        } else {
                            input.take(Constants.UI.MAX_INPUT_PREVIEW_LENGTH) + "..."
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (isInputExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isInputExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                    modifier =
                        if (input.length > Constants.UI.MAX_INPUT_PREVIEW_LENGTH) {
                            Modifier.clickable { isInputExpanded = !isInputExpanded }
                        } else {
                            Modifier
                        }
                )
            }

            // Completed timestamp - right aligned, placed after mission instructions
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.completed_on, formattedDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
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
                    originalInput = Constants.Examples.JSON_INPUT
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
