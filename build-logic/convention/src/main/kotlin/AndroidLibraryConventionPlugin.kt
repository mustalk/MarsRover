import com.android.build.api.dsl.LibraryExtension
import com.mustalk.seat.marsrover.configureKotlinAndroid
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

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                lint.targetSdk = 35
                testOptions.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                buildTypes {
                    release {
                        isMinifyEnabled = false
                    }
                }
            }
        }
    }
}
