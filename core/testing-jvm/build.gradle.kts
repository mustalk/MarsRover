plugins {
    alias(libs.plugins.marsrover.jvm.library)
}

dependencies {
    // Core modules - using api so they're available to test modules that depend on this
    api(project(":core:common"))
    api(project(":core:model"))

    // Essential testing libraries - using api so they're available to dependent modules
    api(libs.kotlinx.coroutines.test)
    api(libs.junit)
    api(libs.mockk)
    api(libs.truth)
}
