plugins {
    alias(libs.plugins.marsrover.jvm.library)
}

dependencies {
    // Core modules
    api(project(":core:model"))
}
