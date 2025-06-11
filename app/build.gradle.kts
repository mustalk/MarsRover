plugins {
    alias(libs.plugins.marsrover.android.application)
    alias(libs.plugins.marsrover.android.application.compose)
    alias(libs.plugins.marsrover.android.hilt)
    alias(libs.plugins.marsrover.quality.gate)
    alias(libs.plugins.marsrover.app.utils)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustalk.seat.marsrover"

    defaultConfig {
        applicationId = "com.mustalk.seat.marsrover"
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "com.mustalk.seat.marsrover.core.testing.android.MarsTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // Core modules (required for app-level DI wiring)
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:mission"))

    // App-specific dependencies not provided by convention plugins or core modules
    implementation(libs.androidx.core.splashscreen)

    // App navigation (not provided by feature convention plugin)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Android integration testing (provides all core testing dependencies via api)
    androidTestImplementation(project(":core:testing-android"))
}
