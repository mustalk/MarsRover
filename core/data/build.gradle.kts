plugins {
    alias(libs.plugins.marsrover.android.library)
    alias(libs.plugins.marsrover.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustalk.seat.marsrover.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.kotlinx.serialization)

    // Hilt for dependency injection (already applied by plugin)
    // implementation(libs.hilt.android)
    // ksp(libs.hilt.compiler)
}
