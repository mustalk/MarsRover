import com.mustalk.seat.marsrover.libs
import com.mustalk.seat.marsrover.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin for enabling Jacoco code coverage on JVM Library modules.
 * Configures test coverage collection for unit tests in pure Kotlin/JVM modules.
 */
class JvmLibraryJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "jacoco")

            configure<JacocoPluginExtension> {
                toolVersion = libs.version("jacoco")
            }

            tasks.withType<JacocoReport>().configureEach {
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }

                // Only configure if this is the test report task
                if (name == "jacocoTestReport") {
                    dependsOn("test")

                    // Set source directories
                    sourceDirectories.setFrom(files("src/main/kotlin"))

                    // Set class directories (excluding common exclusions)
                    classDirectories.setFrom(
                        fileTree("build/classes/kotlin/main") {
                            exclude(
                                "**/*Test*.*",
                                "**/test/**",
                                "**/androidTest/**"
                            )
                        }
                    )

                    // Set execution data from test results
                    executionData.setFrom(fileTree("build/jacoco").include("**/*.exec"))
                }
            }

            tasks.withType<Test>().configureEach {
                configure<JacocoTaskExtension> {
                    // Required for JaCoCo + Robolectric compatibility
                    isIncludeNoLocationClasses = true

                    // Required for JDK 11+ compatibility
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }
}
