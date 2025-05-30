package com.mustalk.seat.marsrover.core.utils

/**
 * Application-wide constants for Mars Rover operations.
 */
object Constants {
    /**
     * Network configuration constants
     */
    object Network {
        const val BASE_URL = "http://localhost:8080/"
        const val SIMULATION_DELAY_MS = 2000L
        const val TIMEOUT_SECONDS = 30L
        const val API_ENDPOINT = "api/v1/mars-rover/execute"
    }

    /**
     * Validation constants
     */
    object Validation {
        const val MAX_PLATEAU_SIZE = Int.MAX_VALUE - 1 // Safety margin
        const val MIN_PLATEAU_SIZE = 0
        const val MAX_POSITION_VALUE = Int.MAX_VALUE - 1 // Safety margin
        const val MIN_POSITION_VALUE = 0

        val VALID_DIRECTIONS = listOf("N", "E", "S", "W")
        val VALID_MOVEMENT_CHARS = listOf('L', 'R', 'M')
    }

    /**
     * UI constants
     */
    object UI {
        const val SUCCESS_MESSAGE_DURATION_MS = 5000L
        const val ERROR_MESSAGE_DURATION_MS = 7000L
        const val LOTTIE_ANIMATION_SIZE_DP = 120
        const val LOTTIE_ANIMATION_SIZE_COMPACT_DP = 80
        const val LOADING_ANIMATION_DURATION_MS = 1000L

        // Layout constants
        const val ALPHA_HALF = 0.5f
        const val ALPHA_INACTIVE_TEXT = 0.7f
        const val LANDSCAPE_CARD_WIDTH_FRACTION = 0.7f
        const val FAB_BOTTOM_PADDING_DP = 80
        const val MAX_INPUT_PREVIEW_LENGTH = 50
    }

    /**
     * HTTP status codes
     */
    object HttpStatus {
        const val OK = 200
        const val BAD_REQUEST = 400
        const val INTERNAL_SERVER_ERROR = 500
    }

    /**
     * Example data for UI
     */
    object Examples {
        const val JSON_INPUT = """{
    "topRightCorner": {"x": 5, "y": 5},
    "roverPosition": {"x": 1, "y": 2},
    "roverDirection": "N",
    "movements": "LMLMLMLMM"
}"""

        const val INPUT_PREVIEW = """{"topRightCorner": {"x": 5, "y": 5}, "roverPosition": {"x": 1, "y": 2}}"""
    }
}
