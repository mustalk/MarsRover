package com.mustalk.seat.marsrover.core.ui.resource

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StringResourceProvider using Android Context.
 * Provides access to string resources.
 */
@Singleton
class StringResourceProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StringResourceProvider {
        override fun getString(
            @StringRes resId: Int,
        ): String = context.getString(resId)

        override fun getString(
            @StringRes resId: Int,
            vararg formatArgs: Any,
        ): String = context.getString(resId, *formatArgs)
    }
