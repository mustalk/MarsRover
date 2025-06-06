plugins {
    alias(libs.plugins.marsrover.android.library)
    alias(libs.plugins.marsrover.android.hilt)
}

android {
    namespace = "com.mustalk.seat.marsrover.core.testing.android"
}

dependencies {
    // Core modules - using api so they're available to test modules that depend on this
    api(project(":core:common"))
    api(project(":core:data"))
    api(project(":core:domain"))
    api(project(":core:model"))

    // Essential testing libraries - using api so they're available to dependent modules
    api(libs.kotlinx.coroutines.test)
    api(libs.junit)
    api(libs.mockk)
    api(libs.truth)
    api(libs.mockwebserver)

    // Android testing dependencies - using api for easy access
    api(libs.androidx.junit)
    api(libs.hilt.android.testing)

    // Test runner for MarsTestRunner (previously provided by espresso)
    implementation(libs.androidx.test.runner)

    // Implementation dependencies for internal test utilities
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
