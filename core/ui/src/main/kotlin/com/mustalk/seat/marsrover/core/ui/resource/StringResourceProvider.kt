package com.mustalk.seat.marsrover.core.ui.resource

import androidx.annotation.StringRes

/**
 * Simple interface for providing string resources in ViewModels.
 * Enables localization while keeping ViewModels clean of Android dependencies.
 */
interface StringResourceProvider {
    /**
     * Get a string resource by its resource ID.
     */
    fun getString(
        @StringRes resId: Int,
    ): String

    /**
     * Get a formatted string resource with the given arguments.
     */
    fun getString(
        @StringRes resId: Int,
        vararg formatArgs: Any,
    ): String
}
