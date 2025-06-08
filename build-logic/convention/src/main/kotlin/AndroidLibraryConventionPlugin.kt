import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.mustalk.seat.marsrover.configureKotlinAndroid
import com.mustalk.seat.marsrover.disableUnnecessaryAndroidTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for Android library modules.
 * Configures common settings like compile SDK, min SDK, Kotlin options, and build features.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            /**
             * Shared configuration can be set up here.
             * This block is intentionally left as a placeholder
             * for common build-type configurations that may
             * be shared across library modules.
             */
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                lint.targetSdk = 35
                testOptions.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                // Configure packaging to exclude conflicting META-INF files
                packaging {
                    resources {
                        excludes.add("/META-INF/{AL2.0,LGPL2.1}")
                        excludes.add("META-INF/LICENSE.md")
                        excludes.add("META-INF/LICENSE-notice.md")
                    }
                }
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                disableUnnecessaryAndroidTests(target)
            }
        }
    }
}
