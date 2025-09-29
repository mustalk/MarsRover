/**
 * Enhanced Test Reporting Utilities
 *
 * This file provides detailed test result collection and reporting functionality
 * for multi-module project testing infrastructure.
 *
 * Key Features:
 * - Automatic test result collection from both unit tests and UI tests
 * - Professional test summaries with module categorization (Android vs JVM)
 * - UI test detection and separate reporting (when available)
 * - Cross-platform clickable coverage report generation
 * - Success rate calculations and detailed statistics
 * - Integration with Jacoco coverage reporting
 *
 * The system intelligently categorizes and displays:
 * - 📱 Android Modules (Unit Tests): Android unit tests only
 * - ☕ JVM Modules: Pure Kotlin/JVM tests
 * - 🔧 UI Tests (Android Instrumented): UI tests when available
 * - 📊 Coverage Reports: Clickable links to generated coverage reports
 *
 * Used by: RootTestingConventionPlugin and Jacoco coverage tasks
 */
package com.mustalk.seat.marsrover

import java.io.File

/**
 * Data class to hold test results for a module
 */
data class ModuleTestResult(
    val modulePath: String,
    val testsRun: Int,
    val testsPassed: Int,
    val testsFailed: Int,
    val testsSkipped: Int,
)

/**
 * Collects test results from both unit tests and UI tests across all modules.
 *
 * This function scans standard test result locations for both JUnit (unit tests)
 * and Android instrumented test results, automatically detecting UI tests by their
 * result file locations and marking them with "(UI)" for separate categorization.
 *
 * @param subprojectPaths Map of module paths to their project directory paths
 * @return TestResults containing both unit test and UI test results
 */
fun collectTestResults(subprojectPaths: Map<String, String>): List<ModuleTestResult> {
    val results = mutableListOf<ModuleTestResult>()

    subprojectPaths.forEach { (modulePath: String, projectDirPath: String) ->
        // Check for Android test results
        val androidTestResultsDir = File(projectDirPath, "build/test-results/testDebugUnitTest")
        if (androidTestResultsDir.exists()) {
            val testResult = parseTestResults(modulePath, androidTestResultsDir)
            if (testResult != null) results.add(testResult)
        }

        // Check for JVM test results
        val jvmTestResultsDir = File(projectDirPath, "build/test-results/test")
        if (jvmTestResultsDir.exists()) {
            val testResult = parseTestResults(modulePath, jvmTestResultsDir)
            if (testResult != null) results.add(testResult)
        }

        // Check for UI test results
        val uiTestResultsDir = File(projectDirPath, "build/outputs/androidTest-results/connected/debug")
        if (uiTestResultsDir.exists()) {
            val testResult = parseTestResults(modulePath, uiTestResultsDir, "UI")
            if (testResult != null) results.add(testResult)
        }
    }

    return results
}

/**
 * Parses test result XML files to extract test statistics
 */
fun parseTestResults(
    modulePath: String,
    testResultsDir: File,
    testType: String = "",
): ModuleTestResult? {
    val xmlFiles = testResultsDir.listFiles { file -> file.extension == "xml" }
    if (xmlFiles.isNullOrEmpty()) return null

    var totalTests = 0
    var totalFailures = 0
    var totalErrors = 0
    var totalSkipped = 0

    xmlFiles.forEach { xmlFile ->
        try {
            val content = xmlFile.readText()
            // Simple regex parsing for test statistics
            val testsPattern = """tests="(\d+)"""".toRegex()
            val failuresPattern = """failures="(\d+)"""".toRegex()
            val errorsPattern = """errors="(\d+)"""".toRegex()
            val skippedPattern = """skipped="(\d+)"""".toRegex()

            testsPattern
                .find(content)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()
                ?.let { totalTests += it }
            failuresPattern
                .find(content)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()
                ?.let { totalFailures += it }
            errorsPattern
                .find(content)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()
                ?.let { totalErrors += it }
            skippedPattern
                .find(content)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()
                ?.let { totalSkipped += it }
        } catch (e: Exception) {
            // Skip files that can't be parsed
        }
    }

    return if (totalTests > 0) {
        val pathWithType = if (testType.isNotEmpty()) "$modulePath ($testType)" else modulePath
        ModuleTestResult(
            modulePath = pathWithType,
            testsRun = totalTests,
            testsPassed = totalTests - totalFailures - totalErrors,
            testsFailed = totalFailures + totalErrors,
            testsSkipped = totalSkipped
        )
    } else {
        null
    }
}

/**
 * Prints test summary with per-module results using dynamic module detection
 */
fun printTestSummary(
    testResults: List<ModuleTestResult>,
    rootProjectDir: File,
    coverageReportPath: String? = null,
) {
    println("\n" + "=".repeat(60))
    println("🧪 TEST EXECUTION SUMMARY")
    println("=".repeat(60))

    // Use dynamic module detection
    val moduleDetection = detectModuleTypes(rootProjectDir)

    val unitTestResults = testResults.filter { !it.modulePath.contains("(UI)") }
    val uiTestResults = testResults.filter { it.modulePath.contains("(UI)") }

    val androidResults =
        unitTestResults.filter { result ->
            moduleDetection.androidModules.any { module -> result.modulePath.startsWith(module) }
        }
    val jvmResults =
        unitTestResults.filter { result ->
            moduleDetection.jvmModules.any { module -> result.modulePath.startsWith(module) }
        }

    // Android modules summary (unit tests)
    if (androidResults.isNotEmpty()) {
        println("\n📱 Android Modules (Unit Tests):")
        androidResults.forEach { result ->
            val status = if (result.testsFailed > 0) "❌" else "✅"
            println(
                "   $status ${result.modulePath}: ${result.testsPassed}/${result.testsRun} passed" +
                    if (result.testsFailed > 0) {
                        " (${result.testsFailed} failed)"
                    } else {
                        "" +
                            if (result.testsSkipped > 0) " (${result.testsSkipped} skipped)" else ""
                    }
            )
        }
    }

    // JVM modules summary
    if (jvmResults.isNotEmpty()) {
        println("\n☕ JVM Modules:")
        jvmResults.forEach { result ->
            val status = if (result.testsFailed > 0) "❌" else "✅"
            println(
                "   $status ${result.modulePath}: ${result.testsPassed}/${result.testsRun} passed" +
                    if (result.testsFailed > 0) {
                        " (${result.testsFailed} failed)"
                    } else {
                        "" +
                            if (result.testsSkipped > 0) " (${result.testsSkipped} skipped)" else ""
                    }
            )
        }
    }

    // UI Test Results (if any)
    if (uiTestResults.isNotEmpty()) {
        println("\n🔧 UI Tests (Android Instrumented):")
        uiTestResults.forEach { result ->
            val status = if (result.testsFailed > 0) "❌" else "✅"
            val moduleName = result.modulePath.replace(" (UI)", "")
            println(
                "   $status $moduleName: ${result.testsPassed}/${result.testsRun} passed" +
                    if (result.testsFailed > 0) {
                        " (${result.testsFailed} failed)"
                    } else {
                        "" +
                            if (result.testsSkipped > 0) " (${result.testsSkipped} skipped)" else ""
                    }
            )
        }
    }

    // Overall summary
    val totalTests = testResults.sumOf { it.testsRun }
    val totalPassed = testResults.sumOf { it.testsPassed }
    val totalFailed = testResults.sumOf { it.testsFailed }
    val totalSkipped = testResults.sumOf { it.testsSkipped }

    println("\n🎯 Overall Summary:")
    if (uiTestResults.isNotEmpty()) {
        val unitTests = unitTestResults.sumOf { it.testsRun }
        val unitPassed = unitTestResults.sumOf { it.testsPassed }
        val uiTests = uiTestResults.sumOf { it.testsRun }
        val uiPassed = uiTestResults.sumOf { it.testsPassed }

        println("   Unit Tests: $unitPassed/$unitTests passed")
        println("   UI Tests: $uiPassed/$uiTests passed")
        println("   Total Tests: $totalPassed/$totalTests passed")
    } else {
        println("   Total Tests: $totalTests")
        println("   Passed: $totalPassed")
        if (totalFailed > 0) println("   Failed: $totalFailed")
        if (totalSkipped > 0) println("   Skipped: $totalSkipped")
    }

    val successRate = if (totalTests > 0) ((totalPassed.toDouble() / totalTests) * 100).toInt() else 0
    val overallStatus = if (totalFailed == 0) "✅ SUCCESS" else "❌ FAILURES"
    println("   Success Rate: $successRate% - $overallStatus")

    // Coverage report if provided
    if (coverageReportPath != null) {
        printClickableCoverageReport(coverageReportPath)
    }

    println("=".repeat(60))
}

/**
 * Creates a clickable file URL that works across different operating systems
 */
fun createClickableFileUrl(filePath: String): String {
    val normalizedPath = filePath.replace("\\", "/")

    return when {
        // Windows: Use file:/// protocol (C:/path/to/file)
        normalizedPath.matches(Regex("^[A-Za-z]:/.*")) -> "file:///$normalizedPath"
        // Unix-like (Linux/macOS): Use file:// protocol (/path/to/file)
        normalizedPath.startsWith("/") -> "file://$normalizedPath"
        // Fallback: assume relative path and make it absolute
        else -> "file:///$normalizedPath"
    }
}

/**
 * Prints a clickable coverage report link with fallback for different terminals
 */
fun printClickableCoverageReport(reportFilePath: String) {
    val clickableUrl = createClickableFileUrl(reportFilePath)

    println("\n📊 Coverage Report:")
    println("   $clickableUrl")

    // Also print a plain path for terminals that don't support clickable links
    println("   📁 Local path: $reportFilePath")
}
