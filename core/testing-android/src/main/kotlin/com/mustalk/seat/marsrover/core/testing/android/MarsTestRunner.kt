package com.mustalk.seat.marsrover.core.testing.android

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner for Mars Rover application.
 *
 * This test runner uses HiltTestApplication to allow Hilt's dependency injection
 * during instrumented tests.
 */
class MarsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
