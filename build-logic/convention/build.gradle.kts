plugins {
    `kotlin-dsl`
}

// Repositories are defined in build-logic/settings.gradle.kts and inherited.
// Defining them here would conflict with RepositoriesMode.FAIL_ON_PROJECT_REPOS.
// repositories {
//     mavenCentral()
//     google()
// }

// Register the convention plugins so they can be used by ID
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "marsrover.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "marsrover.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "marsrover.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "marsrover.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = "marsrover.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "marsrover.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("detektQuality") {
            id = "marsrover.quality.detekt"
            implementationClass = "DetektConventionPlugin"
        }
        register("spotlessQuality") {
            id = "marsrover.quality.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
        register("qualityGate") {
            id = "marsrover.quality.gate"
            implementationClass = "QualityGateConventionPlugin"
        }
    }
}

// Dependencies required to COMPILE the convention plugin classes themselves.
// These are not dependencies FOR the modules applying the plugins, but FOR the plugins.
dependencies {
    compileOnly(gradleApi()) // Standard for Gradle plugin development

    // AGP for ApplicationExtension, LibraryExtension, CommonExtension
    compileOnly(libs.android.gradle.plugin)

    // Kotlin Gradle Plugin for KotlinProjectExtension, KotlinAndroidProjectExtension
    compileOnly(libs.kotlin.gradle.plugin)

    // Compose Gradle Plugin for Compose Compiler configuration
    compileOnly(libs.compose.gradle.plugin)

    // Plugin dependencies needed for extension configuration
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
}
