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
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
            }
        }
    }
}
