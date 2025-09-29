import com.mustalk.seat.marsrover.library
import com.mustalk.seat.marsrover.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for configuring Hilt for Android (application or library) modules.
 * Applies Hilt and KSP plugins and adds common Hilt dependencies.
 * Uses version catalog for consistent dependency management.
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.dagger.hilt.android")
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                add("implementation", libs.library("hilt-android"))
                add("ksp", libs.library("hilt-compiler"))
                add("androidTestImplementation", libs.library("hilt-android-testing"))
                add("kspAndroidTest", libs.library("hilt-compiler"))
            }
        }
    }
}
