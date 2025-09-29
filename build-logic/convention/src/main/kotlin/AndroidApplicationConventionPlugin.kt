import com.android.build.api.dsl.ApplicationExtension
import com.mustalk.seat.marsrover.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for Android application modules.
 * Configures common settings like compile SDK, min/target SDK, Kotlin options, and build features.
 * Namespace, applicationId, and version info should be set in the module's build.gradle.kts.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")

                // Apply quality plugins to all Android application modules
                apply("marsrover.quality.detekt")
                apply("marsrover.quality.spotless")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35

                // Configure packaging to exclude conflicting META-INF files
                packaging {
                    resources {
                        excludes.add("/META-INF/{AL2.0,LGPL2.1}")
                        excludes.add("META-INF/LICENSE.md")
                        excludes.add("META-INF/LICENSE-notice.md")
                    }
                }
            }
        }
    }
}
