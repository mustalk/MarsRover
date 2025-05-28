package com.mustalk.seat.marsrover.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Mars Rover Shape System
 *
 * Defines consistent corner radius values for all UI components.
 * Provides modern, clean shapes suitable for a space/technology theme.
 */
val MarsRoverShapes =
    Shapes(
        // Extra Small - For small chips, badges, or indicators
        extraSmall = RoundedCornerShape(4.dp),
        // Small - For buttons, small cards, text fields
        small = RoundedCornerShape(8.dp),
        // Medium - For cards, dialogs, bottom sheets
        medium = RoundedCornerShape(12.dp),
        // Large - For large cards, modals, major containers
        large = RoundedCornerShape(16.dp),
        // Extra Large - For full-screen overlays
        extraLarge = RoundedCornerShape(28.dp)
    )

/**
 * Additional custom shapes for specific Mars Rover components
 */
object MarsRoverCustomShapes {
    // Completely rounded - For FABs, avatar images, circular buttons
    val circular = RoundedCornerShape(50)

    // Slightly rounded - For subtle borders on containers
    val subtle = RoundedCornerShape(4.dp)

    // Card shape - Standard for most cards and surfaces
    val card = RoundedCornerShape(12.dp)

    // Dialog shape - For modals and dialogs
    val dialog = RoundedCornerShape(16.dp)

    // Button shape - For primary and secondary buttons
    val button = RoundedCornerShape(8.dp)

    // Text field shape - For input fields
    val textField = RoundedCornerShape(8.dp)

    // Top rounded only - For bottom sheets
    val bottomSheet =
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
}
