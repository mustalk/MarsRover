import com.android.build.api.dsl.LibraryExtension
import com.mustalk.seat.marsrover.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * Convention plugin for enabling Jacoco code coverage on Android Library modules.
 * Configures test coverage collection for both unit tests and instrumented tests.
 */
class AndroidLibraryJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "jacoco")

            val androidExtension = extensions.getByType<LibraryExtension>()

            androidExtension.buildTypes.configureEach {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }

            configureJacoco()
        }
    }
}
