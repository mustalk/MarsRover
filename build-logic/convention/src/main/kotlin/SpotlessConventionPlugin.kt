import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for configuring Spotless for code formatting.
 * Applies the Spotless plugin and sets up custom configuration.
 *
 * Note: We intentionally separate Spotless (formatting) from Detekt (analysis).
 * This follows modern best practices where each tool has a single responsibility.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(SpotlessPlugin::class.java)

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("**/*.kt")
                    targetExclude("**/.editorconfig")
                    ktlint().setEditorConfigPath("$rootDir/.editorconfig")
                }

                kotlinGradle {
                    target("**/*.gradle.kts")
                    target("**/*.gradle")
                    ktlint()
                }
            }
        }
    }
}
