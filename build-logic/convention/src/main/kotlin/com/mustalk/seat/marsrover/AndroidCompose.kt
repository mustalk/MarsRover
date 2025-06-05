/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mustalk.seat.marsrover

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options for Android modules.
 *
 * This function sets up:
 * 1. Compose build features and core dependencies (BOM, UI, tooling)
 * 2. Compose compiler configuration with performance optimizations
 * 3. Optional build metrics and reports for development analysis
 * 4. Stability configuration for better runtime performance
 *
 * @param commonExtension The Android extension (ApplicationExtension or LibraryExtension)
 */
internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            // Compose BOM ensures all Compose libraries use compatible versions
            val bom = libs.library("androidx-compose-bom")
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))

            // Core Compose dependencies for all modules using Compose
            "implementation"(libs.library("androidx-ui-tooling-preview"))
            "debugImplementation"(libs.library("androidx-ui-tooling"))
        }
    }

    // Configure Compose Compiler settings for performance optimization
    extensions.configure<ComposeCompilerGradlePluginExtension> {
        /**
         * Helper function to convert gradle property to boolean and only proceed if true.
         * Used for optional development features that should not impact production builds.
         */
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }

        /**
         * Helper function to create relative paths from root project for build outputs.
         * Ensures consistent directory structure for compiler reports across all modules.
         */
        fun Provider<*>.relativeToRootProject(dir: String) =
            map {
                isolated.rootProject.projectDirectory
                    .dir("build")
                    .dir(projectDir.toRelativeString(rootDir))
            }.map { it.dir(dir) }

        /**
         * Enable Compose Compiler Metrics (optional development feature).
         *
         * Generates detailed metrics about Compose compilation including:
         * - Function stability analysis
         * - Skippability reports
         * - Recomposition tracking
         *
         * Usage: Add -PenableComposeCompilerMetrics=true to gradle command
         * Output: build/{module}/compose-metrics/
         */
        project.providers
            .gradleProperty("enableComposeCompilerMetrics")
            .onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        /**
         * Enable Compose Compiler Reports (optional development feature).
         *
         * Generates human-readable reports about Compose compilation including:
         * - Detailed stability analysis
         * - Function parameter analysis
         * - Optimization opportunities
         *
         * Usage: Add -PenableComposeCompilerReports=true to gradle command
         * Output: build/{module}/compose-reports/
         */
        project.providers
            .gradleProperty("enableComposeCompilerReports")
            .onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        /**
         * Configure stability classes for better Compose performance.
         * Context: https://developer.android.com/develop/ui/compose/performance/stability
         *
         * The stability configuration file (compose_compiler_config.conf) tells the Compose
         * compiler which classes should be treated as stable, enabling:
         * - Fewer recompositions
         * - Better runtime performance
         * - Optimized state tracking
         *
         * Classes marked as stable include:
         * - Our core.model.* data classes (immutable by design)
         * - Java standard library classes (ZoneId, ZoneOffset)
         *
         * Reference: https://developer.android.com/jetpack/compose/performance/stability/fix#configuration-file
         */
        stabilityConfigurationFiles
            .add(isolated.rootProject.projectDirectory.file("compose_compiler_config.conf"))
    }
}
