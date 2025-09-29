/**
 * Project Utilities and Module Detection
 *
 * This file provides centralized module detection and project analysis functionality
 * for multi-module convention plugins build systems. It implements intelligent build file analysis to
 * categorize modules and provides utilities for project structure visualization.
 *
 * Key Features:
 * - Dynamic module type detection based on build file content analysis
 * - Build file scanning instead of plugin detection (more reliable)
 * - Automatic exclusion of testing and build-logic modules
 * - Project structure visualization with package path collapsing
 * - Future-proof design that adapts to new modules automatically
 *
 * Module Detection Logic:
 * - Android Modules: Detected by presence of android plugins or specific paths
 * - JVM Modules: Detected by presence of source code without android plugins
 * - Exclusions: Testing modules, build-logic, and gradle-related directories
 *
 * The detection system ensures consistent behavior across all convention plugins
 * and eliminates the need for hardcoded module lists that become outdated.
 *
 * Used by: All convention plugins requiring module information
 */
package com.mustalk.seat.marsrover

import org.gradle.api.Project
import java.io.File

/**
 * Project-specific configuration for plugin detection.
 *
 * This system makes the convention plugins reusable across different projects by automatically
 * detecting or manually configuring the plugin prefix used in your build-logic setup.
 *
 * Configuration Options:
 * 1. AUTO_DETECT_PREFIX: When true, automatically detects prefix from build-logic/convention/build.gradle.kts
 * 2. FALLBACK_PREFIX: Used when auto-detection fails or when AUTO_DETECT_PREFIX is false
 *
 * Usage Scenarios:
 *
 * Scenario 1 - Reuse in New Project (Recommended):
 * - Keep AUTO_DETECT_PREFIX = true
 * - Update FALLBACK_PREFIX to your project's plugin prefix
 * - Ensure your plugin IDs follow the pattern "yourprefix.android.*", "yourprefix.jvm.*", etc.
 *
 * Scenario 2 - Manual Override:
 * - Set AUTO_DETECT_PREFIX = false
 * - Set FALLBACK_PREFIX to your desired prefix
 *
 * Scenario 3 - Multi-Project Setup:
 * - Keep AUTO_DETECT_PREFIX = true (will detect different prefixes per project)
 * - Set FALLBACK_PREFIX as a safe default
 *
 * The auto-detection scans your build-logic/convention/build.gradle.kts for plugin ID patterns
 * and uses the most frequently occurring prefix, making the system very robust and reusable.
 */
private const val AUTO_DETECT_PREFIX = true
private const val FALLBACK_PREFIX = "marsrover"

/**
 * Detects the plugin prefix from build-logic configuration or returns fallback.
 * This makes the convention plugins more reusable across different projects.
 *
 * Detection Strategy:
 * 1. Searches for plugin ID patterns in build-logic/convention/build.gradle.kts
 * 2. Looks for patterns like "prefix.android.*" and "prefix.jvm.*"
 * 3. Returns the most common prefix found, or fallback if none detected
 *
 * @param rootProjectDir [File] the root directory to search for build-logic configuration
 * @return [String] the detected or fallback plugin prefix
 */
private fun detectPluginPrefix(rootProjectDir: File): String {
    if (!AUTO_DETECT_PREFIX) {
        return FALLBACK_PREFIX
    }

    return try {
        val buildLogicConventionFile = File(rootProjectDir, "build-logic/convention/build.gradle.kts")
        if (buildLogicConventionFile.exists()) {
            val content = buildLogicConventionFile.readText()

            // Look for plugin ID patterns like "prefix.android.*" and "prefix.jvm.*"
            val pluginIdRegex = """id\s*=\s*"([^.]+)\.(android|jvm|quality|app)\.""".toRegex()
            val matches = pluginIdRegex.findAll(content)

            // Count occurrences of each prefix to find the most common one
            val prefixCounts =
                matches
                    .map { it.groupValues[1] }
                    .groupingBy { it }
                    .eachCount()

            // Return the most frequent prefix, or fallback if none found
            prefixCounts.maxByOrNull { it.value }?.key ?: FALLBACK_PREFIX
        } else {
            FALLBACK_PREFIX
        }
    } catch (e: Exception) {
        // Fallback to constant if auto-detection fails
        FALLBACK_PREFIX
    }
}

/**
 * Extension functions for project utilities and information gathering.
 */

/**
 * Data class to hold module information for cleaner organization
 */
data class ModuleInfo(
    val path: String,
    val projectDir: File,
    val isAndroid: Boolean,
    val isJvm: Boolean,
    val hasTests: Boolean,
)

/**
 * Data class to hold module detection results
 */
data class ModuleDetectionResult(
    val jvmModules: List<String>,
    val androidModules: List<String>,
)

/**
 * Centralized module detection logic using build file content analysis.
 * This ensures consistency across all convention plugins and provides more reliable
 * detection than plugin-based approaches.
 *
 * Detection Strategy:
 * - Scans build.gradle.kts files for Android plugin references
 * - Checks for actual source code presence in src/main directories
 * - Filters out testing, build-logic, and gradle-related modules
 * - Categorizes modules as Android or JVM based on plugin content
 *
 * @param rootProjectDir [File] the root directory of the project to analyze
 * @return [ModuleDetectionResult] containing categorized module lists
 */
fun detectModuleTypes(rootProjectDir: File): ModuleDetectionResult {
    val jvmModules = mutableListOf<String>()
    val androidModules = mutableListOf<String>()

    // Get the plugin prefix for this project
    val pluginPrefix = detectPluginPrefix(rootProjectDir)

    // Find all module directories (directories with build.gradle.kts)
    val moduleDirectories = mutableListOf<Pair<String, File>>()

    // Add root project
    val rootBuildFile = File(rootProjectDir, "build.gradle.kts")
    if (rootBuildFile.exists()) {
        moduleDirectories.add(":" to rootProjectDir)
    }

    // Find all submodule directories recursively
    findModulesRecursively(rootProjectDir, "", moduleDirectories)

    moduleDirectories.forEach { (modulePath, moduleDir) ->
        // Skip testing modules, root project, and build-logic modules for coverage
        if (modulePath.contains("testing") ||
            modulePath == ":" ||
            modulePath.startsWith(":build-logic") ||
            modulePath.contains("gradle")
        ) {
            return@forEach
        }

        val buildFile = File(moduleDir, "build.gradle.kts")
        val isAndroidModule =
            if (buildFile.exists()) {
                val buildContent = buildFile.readText()
                buildContent.contains("com.android.application") ||
                    buildContent.contains("com.android.library") ||
                    buildContent.contains("$pluginPrefix.android.") ||
                    buildContent.contains("$pluginPrefix.app.") ||
                    modulePath == ":app"
            } else {
                false
            }

        if (isAndroidModule) {
            androidModules.add(modulePath)
        } else {
            // Check if it's a JVM module with actual source code
            val hasKotlinSource =
                File(moduleDir, "src/main/kotlin").exists() ||
                    File(moduleDir, "src/main/java").exists()
            if (hasKotlinSource) {
                jvmModules.add(modulePath)
            }
        }
    }

    return ModuleDetectionResult(
        jvmModules = jvmModules.sorted(),
        androidModules = androidModules.sorted()
    )
}

/**
 * Recursively finds all modules (directories with build.gradle.kts files)
 */
private fun findModulesRecursively(
    currentDir: File,
    currentPath: String,
    moduleDirectories: MutableList<Pair<String, File>>,
) {
    currentDir.listFiles()?.forEach { file ->
        if (file.isDirectory && !file.name.startsWith(".") && file.name != "build") {
            val modulePath = if (currentPath.isEmpty()) ":${file.name}" else "$currentPath:${file.name}"
            val buildFile = File(file, "build.gradle.kts")

            if (buildFile.exists()) {
                moduleDirectories.add(modulePath to file)
            }

            // Continue searching in subdirectories
            findModulesRecursively(file, modulePath, moduleDirectories)
        }
    }
}

/**
 * Dynamically detects all modules in the project and categorizes them (legacy support)
 */
fun Project.detectModules(): List<ModuleInfo> {
    val modules = mutableListOf<ModuleInfo>()

    // Get the plugin prefix for this project
    val pluginPrefix = detectPluginPrefix(rootProject.projectDir)

    // Add all subprojects (skip root)
    rootProject.subprojects.forEach { subproject ->
        if (subproject.path.contains("testing")) {
            // Skip testing modules
            return@forEach
        }

        val buildFile = File(subproject.projectDir, "build.gradle.kts")
        val isAndroidModule =
            if (buildFile.exists()) {
                val buildContent = buildFile.readText()
                buildContent.contains("com.android.application") ||
                    buildContent.contains("com.android.library") ||
                    buildContent.contains("$pluginPrefix.android.") ||
                    buildContent.contains("$pluginPrefix.app.") ||
                    subproject.path == ":app"
            } else {
                false
            }

        val isJvmModule =
            if (!isAndroidModule) {
                // Check if it's a JVM module with actual source code
                File(subproject.projectDir, "src/main/kotlin").exists() ||
                    File(subproject.projectDir, "src/main/java").exists()
            } else {
                false
            }

        val hasTests =
            File(subproject.projectDir, "src/test/kotlin").exists() ||
                File(subproject.projectDir, "src/androidTest/kotlin").exists()

        if (isAndroidModule || isJvmModule) {
            modules.add(
                ModuleInfo(
                    path = subproject.path,
                    projectDir = subproject.projectDir,
                    isAndroid = isAndroidModule,
                    isJvm = isJvmModule,
                    hasTests = hasTests
                )
            )
        }
    }

    return modules.sortedBy { it.path }
}

/**
 * Finds the common base package by analyzing the first available module's source structure.
 *
 * This function traverses the source directory tree to identify the common package prefix
 * that can be collapsed in project structure displays for better readability.
 *
 * Algorithm:
 * - Locates the first module with a src/main/kotlin directory
 * - Follows single-directory paths until branching occurs
 * - Returns the path if it contains at least 3 segments (e.g., com.company.project)
 *
 * @param allProjectInfo [List] of project path to directory mappings
 * @return [String] the common base package path, or null if not detectable
 */
fun findCommonBasePackage(allProjectInfo: List<Pair<String, File>>): String? {
    val sampleModule = allProjectInfo.find { (path, _) -> path != ":" }
    if (sampleModule != null) {
        val kotlinSrc = File(sampleModule.second, "src/main/kotlin")
        if (kotlinSrc.exists()) {
            var current = kotlinSrc
            val pathParts = mutableListOf<String>()

            // Keep going while there's only one directory (until we hit branching or files)
            while (true) {
                val children = current.listFiles()?.filter { it.isDirectory }
                if (children?.size == 1) {
                    val child = children.first()
                    pathParts.add(child.name)
                    current = child
                } else {
                    break
                }
            }

            // Return the path if we have at least 3 parts (like com.company.project)
            return if (pathParts.size >= 3) {
                pathParts.joinToString(".")
            } else {
                null
            }
        }
    }
    return null
}

/**
 * Generates test commands for all modules
 */
fun List<ModuleInfo>.generateTestCommands(): TestCommands {
    val jvmModules = filter { it.isJvm }
    val androidModules = filter { it.isAndroid }

    val jvmTestTasks = jvmModules.joinToString(" ") { "${it.path}:test" }

    return TestCommands(
        jvmModules = jvmModules.map { it.path },
        androidModules = androidModules.map { it.path },
        jvmTestCommand = jvmTestTasks,
        allTestsCommand = "testDebug $jvmTestTasks",
        coverageCommand = "testDebug $jvmTestTasks jacocoOverallAggregatedReport"
    )
}

/**
 * Data class to hold test command information
 */
data class TestCommands(
    val jvmModules: List<String>,
    val androidModules: List<String>,
    val jvmTestCommand: String,
    val allTestsCommand: String,
    val coverageCommand: String,
)

/**
 * Prints detailed directory structure in a tree format
 */
fun printDetailedStructure(
    dir: File,
    prefix: String = "",
    isLast: Boolean = true,
) {
    if (dir.isDirectory && dir.exists()) {
        val children =
            dir
                .listFiles()
                ?.filter { it.isDirectory || it.name.endsWith(".kt") || it.name.endsWith(".java") }
                ?.sorted()

        children?.forEachIndexed { index, file ->
            val isChildLast = index == children.size - 1
            val newPrefix = if (isLast) "$prefix    " else "$prefix|   "
            println("$prefix${if (isChildLast) "|__ " else "|__ "}${file.name}")
            printDetailedStructure(file, newPrefix, isChildLast)
        }
    }
}

/**
 * Prints the content of a single module with base package collapsing
 */
fun printModuleContent(
    projectPath: String,
    projectDir: File,
    basePackage: String?,
) {
    if (projectPath == ":") return

    val sourceSets =
        mapOf(
            "main" to File(projectDir, "src/main/kotlin"),
            "androidTest" to File(projectDir, "src/androidTest/kotlin"),
            "test" to File(projectDir, "src/test/kotlin")
        )

    sourceSets.forEach { (sourceSetName, sourceSetPath) ->
        if (sourceSetPath.exists()) {
            val actualBasePackage =
                if (basePackage != null) {
                    File(sourceSetPath, basePackage.replace(".", "/"))
                } else {
                    sourceSetPath
                }

            if (actualBasePackage.exists()) {
                println("\n$projectPath ($sourceSetName)")

                val children =
                    actualBasePackage
                        .listFiles()
                        ?.filter { it.isDirectory || it.name.endsWith(".kt") || it.name.endsWith(".java") }
                        ?.sorted()

                children?.forEachIndexed { index, file ->
                    val isChildLast = index == children.size - 1
                    printDetailedStructure(file, "|   ", isChildLast)
                }
            }
        }
    }
}

/**
 * Prints project structure in a tree format (legacy support)
 */
fun printDirectoryTree(
    dir: File,
    prefix: String = "",
    isLast: Boolean = true,
) {
    printDetailedStructure(dir, prefix, isLast)
}
