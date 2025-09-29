/**
 * Coverage Exclusions Configuration
 *
 * This file manages Jacoco coverage report exclusions for multi-module Android projects.
 * It provides both static exclusions for common generated code and dynamic detection
 * of project-specific patterns that should be excluded from coverage reports.
 *
 * Key Features:
 * - Static exclusions for Android framework, Hilt, Compose, and other generated code
 * - Dynamic detection of application classes from AndroidManifest.xml and source code
 * - Project-agnostic design that works across different Android projects
 * - Graceful fallback when dynamic detection fails
 *
 * The exclusion system ensures accurate coverage metrics by filtering out:
 * - Framework-generated code that cannot be unit tested
 * - Dependency injection boilerplate (Hilt/Dagger)
 * - UI framework generated classes (Compose, Data Binding)
 * - Application classes and navigation code
 * - Test code itself
 *
 * Used by: Jacoco coverage configuration tasks
 */
package com.mustalk.seat.marsrover

import java.io.File

/**
 * Extensive list of static patterns to exclude from coverage reports.
 *
 * These exclusions filter out generated code and framework files that cannot be unit tested:
 * - Android framework generated files (R.class, BuildConfig, etc.)
 * - Dependency injection framework code (Hilt/Dagger generated classes)
 * - UI framework generated code (Compose compiler output, Data Binding)
 * - Application classes and navigation code (typically not unit testable)
 * - Test code itself (should not be included in production coverage metrics)
 *
 * Patterns use Ant-style wildcards: ** (any directories), * (any characters), \$ (inner classes)
 */
private val staticCoverageExclusions =
    listOf(
        // Android framework generated files
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/androidx/**",
        // Hilt/Dagger dependency injection framework
        "**/Hilt_*.class",
        "**/*_Hilt*.class",
        "**/hilt_aggregated_deps/**",
        "**/hilt_aggregated_deps*/**",
        "hilt_aggregated_deps/**",
        "hilt_aggregated_deps*/**",
        "**/dagger/hilt/**",
        "**/dagger/**",
        "**/*_Factory.class",
        "**/*_MembersInjector.class",
        "**/*Module_*Factory.class",
        "**/*Component.class",
        "**/*_ComponentTreeDeps.class",
        // Android Data Binding & View Binding
        "**/databinding/**",
        "**/BR.class",
        "**/BR\$*.class",
        // Jetpack Compose generated code
        "**/ComposableSingletons*.*",
        "**/ComposableSingletons\$*.*",
        "**/Composables*.*",
        "**/*\$Composable.class",
        "**/*ComposableKt.class",
        "**/*ComposableKt\$*.class",
        "**/LiveLiterals\$*.*",
        // Kotlin compiler generated classes
        "**/*\$WhenMappings.class",
        "**/*\$WhenMappings\$*.class",
        "**/*\$Companion.class",
        "**/*\$Companion\$*.class",
        "**/*\$DefaultImpls.class",
        "**/*\$DefaultImpls\$*.class",
        // Serialization
        "**/*\$\$serializer.class",
        "**/*\$Serializer.class",
        "**/*Kt.class",
        // Application classes (typically not unit testable)
        "**/*Application.class",
        "**/*Application\$*.class",
        // Navigation and generated files (Jetpack Navigation, other routing)
        "**/NavGraphBuilderKt.class",
        "**/NavGraphBuilderKt\$*.class",
        "**/*NavigationKt.class",
        "**/*NavigationKt\$*.class",
        "**/*Navigation.class",
        "**/*Navigation\$*.class",
        "**/*NavGraphKt.class",
        "**/*NavGraphKt\$*.class",
        // Test code exclusions
        "**/*Test*.*",
        "**/test/**",
        "**/androidTest/**",
        "**/*Tests.*",
        "**/*Spec.*"
    )

/**
 * Creates dynamic exclusions based on project-specific patterns.
 *
 * This function automatically detects Application classes and other project-specific
 * generated code that should be excluded from coverage reports.
 *
 * Detection Strategy:
 * 1. Scans AndroidManifest.xml for application class declarations
 * 2. Searches source code for classes extending Application
 * 3. Returns exclusion patterns for detected classes
 *
 * @param rootProjectDir [File] the root directory to scan for project-specific patterns
 * @return [List] of additional exclusion patterns specific to the project
 */
private fun createDynamicExclusions(rootProjectDir: File): List<String> =
    try {
        val dynamicExclusions = mutableListOf<String>()

        // Check for application class name in app manifest or gradle files
        val appModuleDir = File(rootProjectDir, "app")
        if (appModuleDir.exists()) {
            // Scan AndroidManifest.xml for application class declaration
            detectApplicationClassFromManifest(appModuleDir, dynamicExclusions)

            // Scan source code for Application classes
            detectApplicationClassFromSource(appModuleDir, dynamicExclusions)
        }

        dynamicExclusions
    } catch (e: Exception) {
        // If dynamic detection fails, return empty list to avoid breaking the build
        emptyList()
    }

/**
 * Detects application class from AndroidManifest.xml file.
 *
 * Searches for android:name attribute in the <application> tag and extracts
 * the application class name for exclusion.
 *
 * @param appModuleDir [File] the app module directory
 * @param dynamicExclusions [MutableList] list to add detected exclusions to
 */
private fun detectApplicationClassFromManifest(
    appModuleDir: File,
    dynamicExclusions: MutableList<String>,
) {
    val manifestFile = File(appModuleDir, "src/main/AndroidManifest.xml")
    if (manifestFile.exists()) {
        val manifestContent = manifestFile.readText()
        // Match patterns like: android:name=".MyAppApplication" or android:name="com.example.MyAppApplication"
        val applicationNameRegex = """android:name="\.?([^"]*Application)"""".toRegex()
        val match = applicationNameRegex.find(manifestContent)
        if (match != null) {
            val applicationClassName = match.groupValues[1].split(".").last() // Get just the class name
            dynamicExclusions.add("**/$applicationClassName.class")
            dynamicExclusions.add("**/$applicationClassName\$*.class")
        }
    }
}

/**
 * Detects application classes from source code.
 *
 * Searches through Kotlin and Java source files for classes that extend Application
 * and adds them to the exclusion list.
 *
 * @param appModuleDir [File] the app module directory
 * @param dynamicExclusions [MutableList] list to add detected exclusions to
 */
private fun detectApplicationClassFromSource(
    appModuleDir: File,
    dynamicExclusions: MutableList<String>,
) {
    val kotlinSourceDir = File(appModuleDir, "src/main/kotlin")
    val javaSourceDir = File(appModuleDir, "src/main/java")

    listOf(kotlinSourceDir, javaSourceDir).forEach { sourceDir ->
        if (sourceDir.exists()) {
            sourceDir
                .walkTopDown()
                .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                .forEach { file ->
                    val content = file.readText()
                    // Look for class declarations that extend Application
                    if (content.contains("class ") &&
                        (content.contains(": Application()") || content.contains("extends Application"))
                    ) {
                        val classNameRegex = """class\s+(\w+Application)""".toRegex()
                        val match = classNameRegex.find(content)
                        if (match != null) {
                            val className = match.groupValues[1]
                            dynamicExclusions.add("**/$className.class")
                            dynamicExclusions.add("**/$className\$*.class")
                        }
                    }
                }
        }
    }
}

/**
 * Gets the complete list of coverage exclusions including both static and dynamic patterns.
 *
 * This is the main entry point for getting all exclusion patterns. It combines:
 * - Static exclusions for common framework and generated code
 * - Dynamic exclusions detected from the specific project structure
 *
 * @param rootProjectDir [File] the root directory for dynamic exclusion detection
 * @return [List] of all exclusion patterns (static + dynamic)
 */
fun getAllCoverageExclusions(rootProjectDir: File): List<String> = staticCoverageExclusions + createDynamicExclusions(rootProjectDir)

/**
 * Gets only the static coverage exclusions.
 *
 * Useful for debugging or when dynamic detection is not needed.
 *
 * @return [List] of static exclusion patterns
 */
fun getStaticCoverageExclusions(): List<String> = staticCoverageExclusions
