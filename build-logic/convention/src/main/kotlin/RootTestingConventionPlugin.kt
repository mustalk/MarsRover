import com.mustalk.seat.marsrover.collectTestResults
import com.mustalk.seat.marsrover.configureRootJacoco
import com.mustalk.seat.marsrover.createClickableFileUrl
import com.mustalk.seat.marsrover.detectModuleTypes
import com.mustalk.seat.marsrover.printTestSummary
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Root Testing Convention Plugin
 *
 * This plugin creates a testing infrastructure for multi-module Android projects.
 * It provides convenience tasks that orchestrate testing across all modules,
 *   with smart dependency management and enhanced reporting.
 *
 * Key Features:
 * - Dynamic module detection (JVM and Android modules automatically discovered)
 * - Smart test task dependencies (only depends on modules that actually exist)
 * - Enhanced test summaries with module categorization and UI test detection
 * - Coverage integration with clickable report links
 * - Configuration cache compatibility for optimal build performance
 *
 * **Main Testing Tasks:**
 * - `testAllModules`: Unit tests only (JVM + Android unit tests)
 * - `testCompleteSuite`: Unit tests + UI tests (complete suite)
 * - `testAllWithCoverage`: Unit tests + coverage (for development)
 * - `testCompleteSuiteWithCoverage`: Everything + coverage (full validation)
 * - `generateOverallCoverageReport`: Coverage from existing test data (CI optimization)
 *
 * **Utility Tasks:**
 * - `cleanAll`: Project-wide cleanup
 *
 * The plugin uses the centralized module detection from ProjectUtils to ensure consistent
 * behavior across all testing tasks and automatically adapts to new modules.
 */
class RootTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureRootJacoco()
            configureTestTasks()
        }
    }

    private fun Project.configureTestTasks() {
        // Use dynamic module detection
        val moduleDetection = detectModuleTypes(rootProject.projectDir)

        // Capture project directory paths at configuration time for configuration cache compatibility
        val rootProjectDirPath = rootProject.projectDir.absolutePath
        val rootBuildDirPath =
            rootProject.layout.buildDirectory
                .get()
                .asFile.absolutePath
        val subprojectPaths = rootProject.subprojects.associate { it.path to it.projectDir.absolutePath }

        // Detect modules with UI tests (those with src/androidTest directory)
        val androidModulesWithUiTests =
            moduleDetection.androidModules.filter { modulePath ->
                val moduleProjectDir = subprojects.find { it.path == modulePath }?.projectDir
                moduleProjectDir?.let { File(it, "src/androidTest").exists() } ?: false
            }

        // Enhanced clean task for thorough cleaning
        tasks.register("cleanAll") {
            group = "build"
            description = "Thoroughly clean all build directories, cache files, and generated reports"

            doLast {
                println("🧹 Performing thorough project cleanup...")

                // Clean root build directory
                val rootBuildDir = File(rootProjectDirPath, "build")
                if (rootBuildDir.exists()) {
                    println("   Cleaning root build directory...")
                    rootBuildDir.deleteRecursively()
                }

                // Clean all subproject build directories
                subprojectPaths.forEach { (modulePath, moduleProjectDirPath) ->
                    val buildDir = File(moduleProjectDirPath, "build")
                    if (buildDir.exists()) {
                        println("   Cleaning $modulePath build directory...")
                        buildDir.deleteRecursively()
                    }
                }

                println("✅ Project-wide cleanup completed!")
            }
        }

        // Task: Run unit tests across all modules
        tasks.register("testAllModules") {
            group = "testing"
            description = "Run unit tests for all modules (Android + JVM). Use 'testCompleteSuite' for UI tests."

            // Dynamic dependency creation - only depend on modules that actually exist
            // This ensures the task works even if module structure changes
            moduleDetection.androidModules.forEach { modulePath ->
                dependsOn("$modulePath:testDebugUnitTest")
            }
            moduleDetection.jvmModules.forEach { modulePath ->
                dependsOn("$modulePath:test")
            }

            // Enhanced test summary with module categorization
            // Shows breakdown by Android vs JVM modules and provides success statistics
            doLast {
                val testResults = collectTestResults(subprojectPaths)
                printTestSummary(testResults, File(rootProjectDirPath))
            }
        }

        // Task: Unit tests with complete coverage reporting
        tasks.register("testAllWithCoverage") {
            group = "testing"
            description = "Run unit tests across all modules and generate coverage report (unit tests only)"

            // Generate coverage reports for unit tests only (no UI test dependencies)
            // The Jacoco tasks already depend on the unit tests, so we don't need to depend on them again
            dependsOn("jacocoJvmAggregatedReport", "jacocoAndroidUnitTestReport")

            doLast {
                val testResults = collectTestResults(subprojectPaths)
                val jvmReportPath = "$rootBuildDirPath/reports/jacoco/jvm-aggregate/html/index.html"
                val androidReportPath = "$rootBuildDirPath/reports/jacoco/android-aggregate/html/index.html"

                // Show test summary and provide links to both coverage reports
                printTestSummary(testResults, File(rootProjectDirPath))

                println("\n📊 Unit Test Coverage Reports Generated:")
                if (File(jvmReportPath).exists()) {
                    val clickableUrl = createClickableFileUrl(File(jvmReportPath).absolutePath)
                    println("   • JVM Coverage: $clickableUrl")
                }
                if (File(androidReportPath).exists()) {
                    val clickableUrl = createClickableFileUrl(File(androidReportPath).absolutePath)
                    println("   • Android Coverage: $clickableUrl")
                }

                if (!File(jvmReportPath).exists() && !File(androidReportPath).exists()) {
                    println("\n⚠️  Coverage report generation may have failed or is still in progress.")
                }
            }
        }

        // Task to run complete test suite (unit + UI tests)
        tasks.register("testCompleteSuite") {
            group = "testing"
            description = "Run complete test suite (unit + UI tests) across all modules"

            dependsOn("testAllModules")

            // Add UI test dependencies for modules that have androidTest source sets
            androidModulesWithUiTests.forEach { modulePath ->
                dependsOn("$modulePath:connectedDebugAndroidTest")
            }

            doLast {
                val testResults = collectTestResults(subprojectPaths)
                printTestSummary(testResults, File(rootProjectDirPath))
            }
        }

        // Task to run complete test suite with coverage
        tasks.register("testCompleteSuiteWithCoverage") {
            group = "testing"
            description = "Run complete test suite (unit + UI tests) with coverage reporting"

            dependsOn("testAllModules")
            dependsOn("jacocoOverallAggregatedReport")

            // Add UI test dependencies for modules that have androidTest source sets
            androidModulesWithUiTests.forEach { modulePath ->
                dependsOn("$modulePath:connectedDebugAndroidTest")
            }

            // Ensure coverage runs after tests
            tasks.findByName("jacocoOverallAggregatedReport")?.mustRunAfter("testAllModules")

            doLast {
                val testResults = collectTestResults(subprojectPaths)
                val reportPath = "$rootBuildDirPath/reports/jacoco/overall-aggregate/html/index.html"

                // Only print coverage report link if the report actually exists
                if (File(reportPath).exists()) {
                    printTestSummary(testResults, File(rootProjectDirPath), reportPath)
                } else {
                    printTestSummary(testResults, File(rootProjectDirPath))
                    println("\n⚠️  Coverage report generation may have failed or is still in progress.")
                }
            }
        }

        // Task: Generate overall coverage report only (assumes all tests already executed)
        tasks.register("generateOverallCoverageReport") {
            group = "testing"
            description = "Generate overall aggregated coverage report using existing test execution data (CI optimized)"

            // Only depend on the data-only coverage report generation, no test execution
            dependsOn("jacocoOverallAggregatedReportDataOnly")

            doLast {
                val testResults = collectTestResults(subprojectPaths)
                val reportPath = "$rootBuildDirPath/reports/jacoco/overall-aggregate/html/index.html"

                // Only print coverage report link if the report actually exists
                if (File(reportPath).exists()) {
                    printTestSummary(testResults, File(rootProjectDirPath), reportPath)
                } else {
                    printTestSummary(testResults, File(rootProjectDirPath))
                    println("\n⚠️  Coverage report generation may have failed or is still in progress.")
                    println("     Make sure unit tests and UI tests have been executed first.")
                }
            }
        }
    }
}
