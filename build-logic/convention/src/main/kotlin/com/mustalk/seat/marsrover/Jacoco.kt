/**
 * Jacoco Coverage Configuration
 *
 * Multi-module code coverage system with three-tier reporting:
 * 1. JVM-only coverage (domain logic)
 * 2. Android-only coverage (UI and app logic)
 * 3. Overall combined coverage (all modules)
 *
 * Key Features:
 * - Dynamic module detection based on build file analysis
 * - Smart filtering to exclude modules without relevant test sources
 * - Pattern-based execution data collection for device-agnostic coverage
 * - Configuration cache compatibility
 * - Automatic exclusion of generated code
 *
 * The system ensures accurate coverage percentages by only including modules
 * with relevant test sources for each report type.
 */
package com.mustalk.seat.marsrover

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File
import java.util.Locale

private fun String.capitalize() =
    replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

/**
 * Configures Jacoco for Android modules with standardized test coverage collection.
 *
 * This function configures Jacoco plugin settings for Android modules to enable
 * coverage data collection that will be used by aggregated reports at the root level.
 *
 * Key Features:
 * - Configures Jacoco plugin with version from version catalog
 * - Sets up proper JaCoCo + Robolectric compatibility settings
 * - Ensures coverage data collection for all test tasks
 * - Applies JDK 11+ compatibility fixes
 *
 * Usage: Applied automatically to Android modules that use the Jacoco convention plugin
 */
internal fun Project.configureJacoco() {
    // Configure Jacoco plugin with version from version catalog
    configure<JacocoPluginExtension> {
        toolVersion = libs.version("jacoco")
    }

    // Configure all Test tasks to support Jacoco coverage collection
    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            // Required for JaCoCo + Robolectric compatibility
            // https://github.com/robolectric/robolectric/issues/2230
            isIncludeNoLocationClasses = true

            // Required for JDK 11+ with the above setting
            // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
            excludes = listOf("jdk.internal.*")
        }
    }
}

/**
 * Configures a three-tier Jacoco coverage reporting system for the root project.
 *
 * 1. **JVM Aggregate Report** (`jacocoJvmAggregatedReport`)
 *    - Covers pure Kotlin/JVM modules (domain logic, business rules)
 *    - Uses only unit test execution data (.exec files)
 *    - Typically achieves higher coverage percentages due to testable business logic
 *
 * 2. **Android Aggregate Report** (`jacocoAndroidAggregatedReport`)
 *    - Covers Android modules (UI, app logic, data layers)
 *    - Includes only modules that have unit test sources (src/test/kotlin or src/test/java)
 *    - Collects both unit test (.exec) and UI test (.ec) execution data when available
 *    - Excludes modules like :app or :core:ui when only unit tests are run (accurate coverage)
 *
 * 3. **Overall Aggregate Report** (`jacocoOverallAggregatedReport`)
 *    - Combines coverage from both JVM and Android modules
 *    - Depends on individual reports for proper execution ordering
 *    - Provides project-wide coverage metrics
 *    - Uses smart filtering to ensure accurate coverage percentages
 *
 * **Smart Module Filtering:**
 * The system dynamically determines which modules to include based on available test sources:
 * - Modules without unit tests (e.g., :app with only UI tests) are excluded from unit-test-only reports
 * - This prevents untested classes from diluting coverage percentages
 * - When UI tests are available, those modules are automatically included
 *
 * **Configuration Cache Compatibility:**
 * All paths and configurations are captured at configuration time to support Gradle's
 * configuration cache for improved build performance.
 *
 * **Pattern-Based Execution Data Collection:**
 * Uses fileTree with include patterns instead of static file paths to handle:
 * - Device-specific test result directories (e.g., /connected/Device model name/)
 * - Different Android test configurations
 * - Missing execution data gracefully
 */
internal fun Project.configureRootJacoco() {
    // Only apply to root project
    if (this != rootProject) return

    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = libs.version("jacoco")
    }

    // Capture project directory paths at configuration time for configuration cache compatibility
    val subprojectPaths = rootProject.subprojects.associate { it.path to it.projectDir.absolutePath }
    val rootProjectDirPath = rootProject.projectDir.absolutePath
    val rootProjectDir = rootProject.projectDir
    val rootBuildDirPath =
        layout.buildDirectory
            .get()
            .asFile.absolutePath

    // Dynamically detect modules at configuration time using the centralized detection logic
    val moduleDetection = detectModuleTypes(rootProject.projectDir)
    val jvmModulesWithCoverage = moduleDetection.jvmModules.toMutableList()
    val androidModulesWithCoverage = moduleDetection.androidModules.toMutableList()

    // Log detected modules for debugging
    if (jvmModulesWithCoverage.isNotEmpty() || androidModulesWithCoverage.isNotEmpty()) {
        logger.info("📊 Jacoco Coverage - Detected Modules:")
        logger.info("  JVM Modules: ${jvmModulesWithCoverage.joinToString(", ")}")
        logger.info("  Android Modules: ${androidModulesWithCoverage.joinToString(", ")}")
    }

    // JVM Modules Aggregated Report
    tasks.register("jacocoJvmAggregatedReport", JacocoReport::class) {
        configureJvmAggregatedReport(
            project = this@configureRootJacoco,
            subprojectPaths = subprojectPaths,
            rootProjectDirPath = rootProjectDirPath,
            rootProjectDir = rootProjectDir,
            rootBuildDirPath = rootBuildDirPath,
            jvmModulesWithCoverage = jvmModulesWithCoverage
        )
    }

    // Android Modules Aggregated Report
    tasks.register("jacocoAndroidAggregatedReport", JacocoReport::class) {
        configureAndroidAggregatedReport(
            project = this@configureRootJacoco,
            subprojectPaths = subprojectPaths,
            rootProjectDirPath = rootProjectDirPath,
            rootProjectDir = rootProjectDir,
            rootBuildDirPath = rootBuildDirPath,
            androidModulesWithCoverage = androidModulesWithCoverage,
            includeUiTests = true
        )
    }

    // Android Unit-Test-Only Report (for testAllWithCoverage)
    tasks.register("jacocoAndroidUnitTestReport", JacocoReport::class) {
        configureAndroidAggregatedReport(
            project = this@configureRootJacoco,
            subprojectPaths = subprojectPaths,
            rootProjectDirPath = rootProjectDirPath,
            rootProjectDir = rootProjectDir,
            rootBuildDirPath = rootBuildDirPath,
            androidModulesWithCoverage = androidModulesWithCoverage,
            includeUiTests = false
        )
    }

    // Combined Overall Report (combines both JVM and Android modules)
    tasks.register("jacocoOverallAggregatedReport", JacocoReport::class) {
        configureOverallAggregatedReport(
            project = this@configureRootJacoco,
            subprojectPaths = subprojectPaths,
            rootProjectDirPath = rootProjectDirPath,
            rootProjectDir = rootProjectDir,
            rootBuildDirPath = rootBuildDirPath,
            jvmModulesWithCoverage = jvmModulesWithCoverage,
            androidModulesWithCoverage = androidModulesWithCoverage,
            includeDependencies = true
        )
    }

    // Data-Only Overall Report (aggregates existing execution data without running tests)
    tasks.register("jacocoOverallAggregatedReportDataOnly", JacocoReport::class) {
        configureOverallAggregatedReport(
            project = this@configureRootJacoco,
            subprojectPaths = subprojectPaths,
            rootProjectDirPath = rootProjectDirPath,
            rootProjectDir = rootProjectDir,
            rootBuildDirPath = rootBuildDirPath,
            jvmModulesWithCoverage = jvmModulesWithCoverage,
            androidModulesWithCoverage = androidModulesWithCoverage,
            includeDependencies = false
        )
    }
}

/**
 * Configures a JVM aggregated Jacoco report.
 *
 * @param project The project instance for file operations
 * @param subprojectPaths Map of module paths to their project directory paths
 * @param rootProjectDirPath Root project directory path as string
 * @param rootProjectDir Root project directory as File
 * @param rootBuildDirPath Root build directory path as string
 * @param jvmModulesWithCoverage List of JVM modules to include in coverage
 */
private fun JacocoReport.configureJvmAggregatedReport(
    project: Project,
    subprojectPaths: Map<String, String>,
    rootProjectDirPath: String,
    rootProjectDir: File,
    rootBuildDirPath: String,
    jvmModulesWithCoverage: List<String>,
) {
    group = "coverage"
    description = "Generate aggregated coverage report for all JVM modules (domain logic)"

    // Depend on JVM module test tasks (only if they exist)
    jvmModulesWithCoverage.forEach { modulePath ->
        dependsOn("$modulePath:test")
        dependsOn("$modulePath:jacocoTestReport")
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/jvm-aggregate/jacocoJvmAggregatedReport.xml"))
        html.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/jvm-aggregate/html"))
    }

    // Use pattern-based execution data collection
    executionData.setFrom(
        jvmModulesWithCoverage.flatMap { modulePath ->
            val moduleProjectDirPath = subprojectPaths[modulePath]
            if (moduleProjectDirPath != null) {
                // Collect Jacoco execution data using fileTree with include pattern
                // This handles cases where execution data might be in different subdirectories
                // and ensures we only include relevant .exec files
                listOf(project.fileTree("$moduleProjectDirPath/build/jacoco") { include("**/*.exec") })
            } else {
                emptyList()
            }
        }
    )

    // Include both Java and Kotlin source directories from detected JVM modules
    sourceDirectories.setFrom(
        subprojectPaths.filter { it.key in jvmModulesWithCoverage }.flatMap { (_, moduleProjectDirPath) ->
            listOf(
                "$moduleProjectDirPath/src/main/kotlin",
                "$moduleProjectDirPath/src/main/java"
            ).map { project.files(it) }
        }
    )

    // Configure class directories for JVM modules using standard Kotlin/Java output directories
    // Apply the coverage exclusions to filter out generated code
    classDirectories.setFrom(
        subprojectPaths.filter { it.key in jvmModulesWithCoverage }.flatMap { (_, moduleProjectDirPath) ->
            listOf("$moduleProjectDirPath/build/classes/kotlin/main")
                .map { project.fileTree(it) { exclude(getAllCoverageExclusions(rootProjectDir)) } }
        }
    )

    // Enhanced test summary with coverage report integration
    doLast {
        val testResults = collectTestResults(subprojectPaths)
        val reportPath = "$rootBuildDirPath/reports/jacoco/jvm-aggregate/html/index.html"
        printTestSummary(testResults, File(rootProjectDirPath), reportPath)
    }
}

/**
 * Configures an Android aggregated Jacoco report.
 *
 * @param project The project instance for file operations
 * @param subprojectPaths Map of module paths to their project directory paths
 * @param rootProjectDirPath Root project directory path as string
 * @param rootProjectDir Root project directory as File
 * @param rootBuildDirPath Root build directory path as string
 * @param androidModulesWithCoverage List of Android modules to include in coverage
 * @param includeUiTests Whether to include UI tests in the report
 */
private fun JacocoReport.configureAndroidAggregatedReport(
    project: Project,
    subprojectPaths: Map<String, String>,
    rootProjectDirPath: String,
    rootProjectDir: File,
    rootBuildDirPath: String,
    androidModulesWithCoverage: List<String>,
    includeUiTests: Boolean,
) {
    group = "coverage"
    description =
        if (includeUiTests) {
            "Generate aggregated coverage report for all Android modules (app + UI) including UI test data when available"
        } else {
            "Generate aggregated coverage report for all Android modules (unit tests only)"
        }

    // Depend on Android module test tasks (only if they exist)
    androidModulesWithCoverage.forEach { modulePath ->
        // All Android modules should have testDebugUnitTest
        dependsOn("$modulePath:testDebugUnitTest")

        // Add dependency on UI test tasks if they exist to avoid implicit dependency issues
        // This ensures execution order when UI test data is used in coverage calculation
        if (includeUiTests) {
            if (project.rootProject
                    .findProject(modulePath)
                    ?.tasks
                    ?.findByName("connectedDebugAndroidTest") != null
            ) {
                dependsOn("$modulePath:connectedDebugAndroidTest")
            }
        }
    }

    // Force the task to run if there are Android modules to process
    onlyIf { androidModulesWithCoverage.isNotEmpty() }

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/android-aggregate/jacocoAndroidAggregatedReport.xml"))
        html.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/android-aggregate/html"))
    }

    executionData.setFrom(
        androidModulesWithCoverage.flatMap { modulePath ->
            val moduleProjectDirPath = subprojectPaths[modulePath]
            if (moduleProjectDirPath != null) {
                val execFiles =
                    listOf(
                        project.fileTree("$moduleProjectDirPath/build/outputs/unit_test_code_coverage/debugUnitTest") {
                            include("**/*.exec")
                        }
                    )

                if (includeUiTests) {
                    execFiles +
                        listOf(
                            project.fileTree("$moduleProjectDirPath/build/outputs/code_coverage/debugAndroidTest") {
                                include("**/*.ec")
                            }
                        )
                } else {
                    execFiles
                }
            } else {
                emptyList()
            }
        }
    )

    sourceDirectories.setFrom(
        subprojectPaths.filter { it.key in androidModulesWithCoverage }.flatMap { (_, moduleProjectDirPath) ->
            listOf(
                "$moduleProjectDirPath/src/main/kotlin",
                "$moduleProjectDirPath/src/main/java"
            ).map { project.files(it) }
        }
    )
    classDirectories.setFrom(
        subprojectPaths.filter { it.key in androidModulesWithCoverage }.flatMap { (_, moduleProjectDirPath) ->
            // Smart class directory filtering - only include modules that have unit tests
            // This prevents untested modules (like :app with only UI tests) from diluting coverage percentages
            val hasUnitTests =
                moduleProjectDirPath != null &&
                    (File("$moduleProjectDirPath/src/test/java").exists() || File("$moduleProjectDirPath/src/test/kotlin").exists())

            if (hasUnitTests) {
                listOf(
                    "$moduleProjectDirPath/build/tmp/kotlin-classes/debug",
                    "$moduleProjectDirPath/build/intermediates/javac/debug/classes",
                    "$moduleProjectDirPath/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes"
                ).map { project.fileTree(it) { exclude(getAllCoverageExclusions(rootProjectDir)) } }
            } else {
                emptyList()
            }
        }
    )

    // Add enhanced test summary with coverage report integration
    doLast {
        val testResults = collectTestResults(subprojectPaths)
        val reportPath = "$rootBuildDirPath/reports/jacoco/android-aggregate/html/index.html"
        printTestSummary(testResults, File(rootProjectDirPath), reportPath)
    }
}

/**
 * Configures an overall aggregated Jacoco report with optional test dependencies.
 *
 * This function centralizes the configuration for overall coverage reports, allowing
 * the same report structure to be used with or without test execution dependencies.
 *
 * @param project The project instance for file operations
 * @param subprojectPaths Map of module paths to their project directory paths
 * @param rootProjectDirPath Root project directory path as string
 * @param rootProjectDir Root project directory as File
 * @param rootBuildDirPath Root build directory path as string
 * @param jvmModulesWithCoverage List of JVM modules to include in coverage
 * @param androidModulesWithCoverage List of Android modules to include in coverage
 * @param includeDependencies Whether to include test task dependencies (true for normal execution, false for CI data-only)
 */
private fun JacocoReport.configureOverallAggregatedReport(
    project: Project,
    subprojectPaths: Map<String, String>,
    rootProjectDirPath: String,
    rootProjectDir: File,
    rootBuildDirPath: String,
    jvmModulesWithCoverage: List<String>,
    androidModulesWithCoverage: List<String>,
    includeDependencies: Boolean,
) {
    val taskLogger = project.logger // Capture logger at configuration time

    group = "coverage"
    description =
        if (includeDependencies) {
            "Generate overall aggregated coverage report combining JVM and Android modules including UI test data when available"
        } else {
            "Generate overall aggregated coverage report using existing execution data only (CI optimized)"
        }

    // Conditionally add test dependencies
    if (includeDependencies) {
        // Depend on JVM module test tasks (only if they exist)
        jvmModulesWithCoverage.forEach { modulePath ->
            dependsOn("$modulePath:test")
        }
        androidModulesWithCoverage.forEach { modulePath ->
            dependsOn("$modulePath:testDebugUnitTest")
        }

        // Also depend on individual aggregated reports for better organization
        dependsOn("jacocoJvmAggregatedReport", "jacocoAndroidAggregatedReport")
    } else {
        // Only execute if execution data exists (prevents errors when no tests have been run)
        onlyIf {
            val hasExecutionData =
                jvmModulesWithCoverage.any { modulePath ->
                    val moduleProjectDirPath = subprojectPaths[modulePath]
                    moduleProjectDirPath?.let {
                        File("$it/build/jacoco").exists()
                    } ?: false
                } ||
                    androidModulesWithCoverage.any { modulePath ->
                        val moduleProjectDirPath = subprojectPaths[modulePath]
                        moduleProjectDirPath?.let {
                            File("$it/build/outputs/unit_test_code_coverage/debugUnitTest").exists() ||
                                File("$it/build/outputs/code_coverage/debugAndroidTest").exists()
                        } ?: false
                    }

            if (!hasExecutionData) {
                taskLogger.warn("⚠️  No execution data found. Make sure unit tests and/or UI tests have been executed first.")
            }

            hasExecutionData
        }
    }

    // Common report configuration
    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/overall-aggregate/jacocoOverallAggregatedReport.xml"))
        html.outputLocation.set(project.file("$rootBuildDirPath/reports/jacoco/overall-aggregate/html"))
    }

    // Use pattern-based execution data collection
    executionData.setFrom(
        // JVM execution data
        jvmModulesWithCoverage.flatMap { modulePath ->
            val moduleProjectDirPath = subprojectPaths[modulePath]
            if (moduleProjectDirPath != null) {
                listOf(project.fileTree("$moduleProjectDirPath/build/jacoco") { include("**/*.exec") })
            } else {
                emptyList()
            }
        } +
            // Android execution data (both unit and UI tests when available)
            androidModulesWithCoverage.flatMap { modulePath ->
                val moduleProjectDirPath = subprojectPaths[modulePath]
                if (moduleProjectDirPath != null) {
                    listOf(
                        project.fileTree("$moduleProjectDirPath/build/outputs/unit_test_code_coverage/debugUnitTest") { include("**/*.exec") },
                        project.fileTree("$moduleProjectDirPath/build/outputs/code_coverage/debugAndroidTest") { include("**/*.ec") }
                    )
                } else {
                    emptyList()
                }
            }
    )

    // Source directories configuration
    sourceDirectories.setFrom(
        subprojectPaths.filter { it.key in (jvmModulesWithCoverage + androidModulesWithCoverage) }.flatMap { (_, moduleProjectDirPath) ->
            listOf(
                "$moduleProjectDirPath/src/main/kotlin",
                "$moduleProjectDirPath/src/main/java"
            ).map { project.files(it) }
        }
    )

    // Class directories configuration with smart filtering
    classDirectories.setFrom(
        subprojectPaths.filter { it.key in (jvmModulesWithCoverage + androidModulesWithCoverage) }.flatMap { (modulePath, moduleProjectDirPath) ->
            when {
                modulePath in jvmModulesWithCoverage -> {
                    listOf(
                        "$moduleProjectDirPath/build/classes/kotlin/main"
                    ).map { project.fileTree(it) { exclude(getAllCoverageExclusions(rootProjectDir)) } }
                }

                else -> {
                    // Only include class directories for Android modules that have unit tests
                    val hasUnitTests =
                        moduleProjectDirPath != null &&
                            (File("$moduleProjectDirPath/src/test/java").exists() || File("$moduleProjectDirPath/src/test/kotlin").exists())

                    if (hasUnitTests) {
                        listOf(
                            "$moduleProjectDirPath/build/tmp/kotlin-classes/debug",
                            "$moduleProjectDirPath/build/intermediates/javac/debug/classes",
                            "$moduleProjectDirPath/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes"
                        ).map { project.fileTree(it) { exclude(getAllCoverageExclusions(rootProjectDir)) } }
                    } else {
                        emptyList()
                    }
                }
            }
        }
    )

    // Common post-processing
    doLast {
        // Post-processing and enhanced reporting
        val htmlReportDir = File("$rootBuildDirPath/reports/jacoco/overall-aggregate/html")
        if (htmlReportDir.exists()) {
            // Clean up Hilt generated directories from HTML report for better readability
            htmlReportDir.walkTopDown().forEach { file ->
                if (file.isDirectory && (file.name.startsWith("hilt_aggregated_deps") || file.name.contains("hilt_aggregated_deps"))) {
                    taskLogger.info("🧹 Removing Hilt directory from HTML report: ${file.absolutePath}")
                    file.deleteRecursively()
                }
            }
        }

        // Generate enhanced test summary with coverage report integration (only for dependency-enabled version)
        if (includeDependencies) {
            val testResults = collectTestResults(subprojectPaths)
            val reportPath = "$rootBuildDirPath/reports/jacoco/overall-aggregate/html/index.html"
            printTestSummary(testResults, File(rootProjectDirPath), reportPath)
        }
    }
}
