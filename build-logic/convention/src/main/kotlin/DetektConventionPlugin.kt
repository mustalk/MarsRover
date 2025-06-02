import com.mustalk.seat.marsrover.libs
import com.mustalk.seat.marsrover.version
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/**
 * Convention plugin for configuring Detekt for static code analysis.
 * Applies the Detekt plugin and sets up custom configuration from `config/detekt/detekt.yml`.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(DetektPlugin::class.java)

            extensions.configure<DetektExtension> {
                toolVersion = libs.version("detekt-plugin")
                basePath = projectDir.absolutePath
                parallel = true
                config.setFrom("$rootDir/config/detekt/detekt.yml")
                buildUponDefaultConfig = true
                debug = false
                ignoreFailures = false
                autoCorrect = true
                ignoredBuildTypes = listOf("release")
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(false)
                }
            }
        }
    }
}
