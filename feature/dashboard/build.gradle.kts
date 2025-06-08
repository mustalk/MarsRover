plugins {
    alias(libs.plugins.marsrover.android.feature)
}

android {
    namespace = "com.mustalk.seat.marsrover.feature.dashboard"
}

// All common feature dependencies are provided by AndroidFeatureConventionPlugin
// Only add feature-specific dependencies here if needed
