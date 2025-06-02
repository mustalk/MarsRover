import com.mustalk.seat.marsrover.configureKotlinJvm
import com.mustalk.seat.marsrover.library
import com.mustalk.seat.marsrover.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for pure Kotlin/JVM library modules (non-Android).
 * Configures common settings like Java version and Kotlin JVM target.
 * This is suitable for modules like `:domain` or `:core-common`.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.jvm")

            configureKotlinJvm()
            dependencies {
                "testImplementation"(libs.library("junit"))
                "testImplementation"(libs.library("mockk"))
                "testImplementation"(libs.library("truth"))
                "testImplementation"(libs.library("mockwebserver"))
                "testImplementation"(libs.library("kotlinx-coroutines-test"))
            }
        }
    }
}
