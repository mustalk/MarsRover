import com.mustalk.seat.marsrover.detectModuleTypes
import com.mustalk.seat.marsrover.findCommonBasePackage
import com.mustalk.seat.marsrover.printModuleContent
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Project Utilities Convention Plugin
 *
 * Provides essential project utilities and information commands for
 * multi-module Android projects.
 *
 * **Features:**
 * - Automatic git hooks installation for code quality enforcement
 * - Dynamic project structure visualization with package analysis
 * - Test execution guidance with module breakdown
 * - Coverage reporting information
 *
 * **Created Tasks (project-info group):**
 * - `printProjectStructure`: Display complete Project structure with namespace detection
 * - `showCoverageInfo`: Coverage commands and module information
 * - `showTestInfo`: Test execution guidance
 *
 * The plugin uses dynamic module detection to automatically adapt to project changes
 */
class ProjectUtilsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            setupGitHooks()
            setupUtilityTasks()
        }
    }

    private fun Project.setupGitHooks() {
        // Install git hooks automatically
        gradle.taskGraph.whenReady {
            val from = File("${rootProject.rootDir}/config/pre-commit")
            val to = File("${rootProject.rootDir}/.git/hooks/pre-commit")
            if (from.exists()) {
                from.copyTo(to, overwrite = true)
                to.setExecutable(true)
            }
        }
    }

    private fun Project.setupUtilityTasks() {
        // Task to print project structure for all modules
        tasks.register("printProjectStructure") {
            group = "project-info"
            description = "Print the multi-module project structure with source sets"

            // Detect modules and base package at configuration time
            val allProjectInfo = mutableListOf<Pair<String, File>>()
            allProjectInfo.add(":" to project.rootProject.projectDir)
            project.rootProject.subprojects.forEach { subproject ->
                allProjectInfo.add(subproject.path to subproject.projectDir)
            }

            doLast {
                println("Project Structure:")
                println("==================")

                val basePackage = findCommonBasePackage(allProjectInfo)

                if (basePackage != null) {
                    println("Base Package: $basePackage")
                    println("${"-".repeat(50)}")
                }

                allProjectInfo.sortedBy { it.first }.forEach { (projectPath, projectDir) ->
                    printModuleContent(projectPath, projectDir, basePackage)
                }
            }
        }

        // Task to display coverage information
        tasks.register("showCoverageInfo") {
            group = "project-info"
            description = "Display test coverage commands and module breakdown"

            // Use dynamic module detection at configuration time
            val moduleDetection = detectModuleTypes(project.rootProject.projectDir)

            doLast {
                println("Test Coverage Commands and Module Information")
                println("=".repeat(50))

                println("📱 Android Modules:")
                moduleDetection.androidModules.forEach { module ->
                    println("  ✓ $module")
                }

                println("\n☕ JVM Modules:")
                moduleDetection.jvmModules.forEach { module ->
                    println("  ✓ $module")
                }

                println("\n📊 Coverage Reports Available:")

                println("\n1. JVM Only Coverage (Domain Logic - Unit Tests Only):")
                println("   ./gradlew jacocoJvmAggregatedReport")
                println("   Report: build/reports/jacoco/jvm-aggregate/html/index.html")

                println("\n2. Android Only Coverage (App + UI - Unit Tests Only):")
                println("   ./gradlew jacocoAndroidAggregatedReport")
                println("   Report: build/reports/jacoco/android-aggregate/html/index.html")

                println("\n3. Overall Combined Coverage (All Modules - Unit Tests Only):")
                println("   ./gradlew jacocoOverallAggregatedReport")
                println("   Report: build/reports/jacoco/overall-aggregate/html/index.html")

                println("\n🚀 Convenience Tasks:")
                println("   ./gradlew testAllModules")
                println("     ↳ Run all unit tests across modules")

                println("   ./gradlew testAllWithCoverage")
                println("     ↳ Run all unit tests + generate overall coverage")

                println("   ./gradlew testCompleteSuite")
                println("     ↳ Run unit tests + UI tests (requires emulator/device)")

                println("   ./gradlew testCompleteSuiteWithCoverage")
                println("     ↳ Run unit tests + UI tests + generate coverage")

                println("\n📝 Notes:")
                println("   • --parallel: Faster execution using multi-module setup (used by default if setup on 'gradle.properties')")
                println("   • --configuration-cache: Optional, improves build performance but can be tricky")
                println("   • --rerun-tasks: Forces re-execution of tasks, useful for clean coverage reports generation")
                println("   • UI tests require emulator or connected device")
                println("   • Coverage excludes generated code (Hilt, Compose, etc.)")
                println("   • For clean coverage: ./gradlew cleanAll followed by test command")
            }
        }

        // Task to display test information
        tasks.register("showTestInfo") {
            group = "project-info"
            description = "Display test execution commands for all modules"

            // Use dynamic module detection at configuration time
            val moduleDetection = detectModuleTypes(project.rootProject.projectDir)

            doLast {
                println("Test Execution Commands for All Modules")
                println("=".repeat(50))

                println("📊 Module Breakdown:")
                println("   Android modules: ${moduleDetection.androidModules.size} (${moduleDetection.androidModules.joinToString(", ")})")
                println("   JVM modules: ${moduleDetection.jvmModules.size} (${moduleDetection.jvmModules.joinToString(", ")})")

                println("\n🚀 Available Test Commands:")

                println("\n📱 Unit Tests Only:")
                println("   ./gradlew testAllModules")
                println("     ↳ Run unit tests across all ${moduleDetection.androidModules.size + moduleDetection.jvmModules.size} modules")

                println("\n🔧 Complete Test Suite (Unit + UI Tests):")
                println("   ./gradlew testCompleteSuite")
                println("     ↳ Run unit tests + UI tests (requires emulator/device)")

                println("\n📋 Tests + Coverage:")
                println("   ./gradlew testAllWithCoverage")
                println("     ↳ Unit tests + coverage report (unit tests only)")
                println("   ./gradlew testCompleteSuiteWithCoverage")
                println("     ↳ Unit + UI tests + coverage report")

                println("\n📊 Coverage Only:")
                println("   ./gradlew generateOverallCoverageReport")
                println("     ↳ Generate coverage from existing test data")

                println("\n🧹 Cleanup:")
                println("   ./gradlew cleanAll")
                println("     ↳ Complete project-wide cleanup")

                println("\n📝 Notes:")
                println("   • Unit tests: Fast, no device required")
                println("   • UI tests: Require Android emulator or connected device")
                println("   • generateOverallCoverageReport: Assumes all tests executed (CI memory optimization)")
                println("   • --parallel: Faster execution using multi-module setup (used by default if setup on 'gradle.properties')")
                println("   • --rerun-tasks:  Forces re-execution of tasks, useful for debugging and clean coverage reports generation.")
            }
        }
    }
}
