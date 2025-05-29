package com.mustalk.seat.marsrover.presentation.ui.dashboard.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.mustalk.seat.marsrover.R
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme

/**
 * Floating Action Button for starting a new rover mission.
 * Available in both regular and extended variants.
 */
@Composable
fun NewMissionFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = false,
) {
    val contentDesc = stringResource(R.string.cd_mission_fab)

    if (extended) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier.semantics { contentDescription = contentDesc },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.mission_new),
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.semantics { contentDescription = contentDesc },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}

@Preview(name = "New Mission FAB - Regular", showBackground = true)
@Composable
private fun NewMissionFabPreview() {
    MarsRoverTheme {
        NewMissionFab(
            onClick = { },
            extended = false
        )
    }
}

@Preview(name = "New Mission FAB - Extended", showBackground = true)
@Composable
private fun NewMissionFabExtendedPreview() {
    MarsRoverTheme {
        NewMissionFab(
            onClick = { },
            extended = true
        )
    }
}
