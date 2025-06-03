plugins {
    alias(libs.plugins.marsrover.jvm.library)
}

dependencies {
    // Core modules - using api since domain exposes model types in public interfaces
    api(project(":core:model"))
    api(project(":core:common"))
}
