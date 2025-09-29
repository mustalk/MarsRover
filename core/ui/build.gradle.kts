plugins {
    alias(libs.plugins.marsrover.android.library)
    alias(libs.plugins.marsrover.android.library.compose)
    alias(libs.plugins.marsrover.android.library.jacoco)
    alias(libs.plugins.marsrover.android.hilt)
}

android {
    namespace = "com.mustalk.seat.marsrover.core.ui"
}

dependencies {
    // Core Android dependencies
    api(libs.androidx.material3)

    // Lifecycle - expose to consuming modules
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.activity.compose)

    // Lottie for animations - expose to consuming modules
    api(libs.lottie.compose)

    // Android testing dependencies - expose to consuming modules
    androidTestImplementation(libs.androidx.junit)
}
