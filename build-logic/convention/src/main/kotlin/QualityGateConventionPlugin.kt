import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin for configuring quality gates that run before builds.
 * Enables automatic code quality checks on preBuild for debug builds (development).
 * Can be disabled for CI builds by setting -PskipQualityGate=true.
 */
class QualityGateConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("marsrover.quality.detekt")
                apply("marsrover.quality.spotless")
            }

            // Only apply if quality gate is not explicitly skipped (useful for CI)
            val skipQualityGate = project.findProperty("skipQualityGate")?.toString()?.toBoolean() ?: false

            if (!skipQualityGate) {
                afterEvaluate {
                    // Configure preBuild dependencies for quality checks
                    tasks.findByName("preBuild")?.let { preBuildTask ->
                        preBuildTask.dependsOn("detekt")
                        preBuildTask.dependsOn("spotlessCheck")
                    }

                    // Create convenience tasks grouped under "codestyle"
                    createCodeStyleTasks()
                }
            }
        }
    }

    private fun Project.createCodeStyleTasks() {
        tasks.register("runDetekt") {
            description = "Runs Detekt code analysis."
            group = "code-quality"
            dependsOn("detekt")
            doLast {
                logger.lifecycle("Detekt analysis completed.")
            }
        }

        tasks.register("runSpotlessCheck") {
            description = "Runs Spotless code style check."
            group = "code-quality"
            dependsOn("spotlessCheck")
            doLast {
                logger.lifecycle("Spotless check completed.")
            }
        }

        tasks.register("runSpotlessFormat") {
            description = "Runs Spotless code formatting."
            group = "code-quality"
            dependsOn("spotlessApply")
            doLast {
                logger.lifecycle("Spotless formatting completed.")
            }
        }
    }
}
