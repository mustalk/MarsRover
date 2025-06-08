import com.android.build.api.dsl.LibraryExtension
import com.mustalk.seat.marsrover.library
import com.mustalk.seat.marsrover.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for feature modules.
 * Configures feature modules with Android library, Compose UI, and Hilt DI.
 * Includes common dependencies shared across feature modules using version catalog.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "marsrover.android.library")
            apply(plugin = "marsrover.android.library.compose")
            apply(plugin = "marsrover.android.hilt")

            extensions.configure<LibraryExtension> {
                testOptions.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "com.mustalk.seat.marsrover.core.testing.android.MarsTestRunner"
            }

            // Common dependencies for all feature modules using version catalog
            dependencies {
                // Core modules (common to all features)
                "implementation"(project(":core:model"))
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:ui"))

                // UI and Core AndroidX
                "implementation"(libs.library("androidx-core-ktx"))
                "implementation"(libs.library("androidx-lifecycle-runtime-ktx"))
                "implementation"(libs.library("androidx-activity-compose"))
                "implementation"(platform(libs.library("androidx-compose-bom")))
                "implementation"(libs.library("androidx-ui"))
                "implementation"(libs.library("androidx-ui-graphics"))
                "implementation"(libs.library("androidx-ui-tooling-preview"))
                "implementation"(libs.library("androidx-material3"))

                // Navigation (common to all features)
                "implementation"(libs.library("androidx-navigation-compose"))
                "implementation"(libs.library("hilt-navigation-compose"))

                // ViewModel (common to all features)
                "implementation"(libs.library("androidx-lifecycle-viewmodel-ktx"))
                "implementation"(libs.library("androidx-lifecycle-viewmodel-compose"))

                // Testing infrastructure (common to all features)
                "testImplementation"(project(":core:testing-jvm"))

                // Android testing infrastructure
                "androidTestImplementation"(project(":core:testing-jvm"))
                "androidTestImplementation"(project(":core:testing-android"))
                "androidTestImplementation"(platform(libs.library("androidx-compose-bom")))
                "androidTestImplementation"(libs.library("androidx-ui-test-junit4"))

                // Debug tools (common for all features)
                "debugImplementation"(libs.library("androidx-ui-tooling"))
                "debugImplementation"(libs.library("androidx-ui-test-manifest"))
            }
        }
    }
}
