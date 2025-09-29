# Convention Plugins - Build Logic

This directory contains the Gradle convention plugins for multi-module Android projects.

## Overview

The build logic implements a production-ready development infrastructure with:

- **Smart Multi-Module Management**: Dynamic module detection and dependency resolution
- **Advanced Coverage Reporting**: Three-tier Jacoco system with intelligent filtering
- **Enhanced Test Infrastructure**: Unified testing across JVM and Android modules
- **Developer Experience Tools**: Detailed utilities and information commands
- **Performance Optimization**: Configuration cache compatibility and parallel execution

## Convention Plugins

### 🏗️ Core Infrastructure

#### `AndroidApplicationConventionPlugin`

- Configures Android application modules with production-ready settings
- Sets up Jetpack Compose, Kotlin compilation, and build configurations

#### `AndroidLibraryConventionPlugin`

- Configures Android library modules with standardized settings
- Handles shared Android configurations across library modules
- Supports both UI and non-UI library types

#### `JvmLibraryConventionPlugin`

- Configures pure Kotlin/JVM library modules
- Sets up domain logic and business rule modules
- Provides clean separation between platform and business logic

### 📊 Testing & Coverage System

#### `RootTestingConventionPlugin`

**Multi-module testing orchestration with enhanced reporting**

**Key Features:**

- **Dynamic Module Detection**: Automatically discovers and adapts to project structure changes
- **Smart Task Dependencies**: Only depends on modules that actually exist
- **Enhanced Test Summaries**: Professional reporting with module categorization
- **UI Test Integration**: Automatic detection and separate reporting of instrumented tests
- **Coverage Integration**: Seamless integration with Jacoco coverage reports

**Created Tasks:**

```gradle
./gradlew testAllModules                   // Unit tests only (Android + JVM)
./gradlew testAllWithCoverage              // Unit tests + coverage
./gradlew testCompleteSuite                // Unit + UI tests
./gradlew testCompleteSuiteWithCoverage    // Complete suite + coverage
./gradlew cleanAll                         // Complete project cleanup
```

**Enhanced Test Reporting:**

- 📱 Android Modules (Unit Tests): Clear categorization of Android unit tests
- ☕ JVM Modules: Pure Kotlin/JVM test results
- 🔧 UI Tests (Android Instrumented): Separate UI test reporting when available
- 📊 Coverage Reports: Clickable links to generated coverage reports

#### **Advanced Jacoco Coverage System** (`Jacoco.kt`)

**Three-tier coverage reporting with intelligent module filtering**

**Architecture:**

1. **JVM Aggregate Report** (`jacocoJvmAggregatedReport`)
    - Domain logic coverage (pure Kotlin/JVM modules)
    - Typically achieves higher coverage due to testable business logic
    - Uses only unit test execution data

2. **Android Aggregate Report** (`jacocoAndroidAggregatedReport`)
    - **Smart Module Filtering**: Only includes modules with unit test sources
    - **Prevents Coverage Dilution**: Excludes modules like `:app` that only have UI tests
    - **Adaptive Data Collection**: Includes UI test data when available
    - **Accurate Percentages**: Results in realistic coverage metrics

3. **Overall Aggregate Report** (`jacocoOverallAggregatedReport`)
    - Combines JVM and Android coverage with smart filtering
    - Depends on individual reports for proper execution ordering
    - Provides project-wide coverage metrics

**Key Innovations:**

- **Pattern-Based Execution Data**: Device-agnostic collection using `fileTree("dir") { include("**/*.exec") }`
- **Configuration Cache Compatible**: All paths captured at configuration time
- **Exclusions**: Filters generated code (Hilt, Compose, Android framework)
- **HTML Report Cleanup**: Post-processing removes clutter from generated code

### 🛠️ Developer Utilities

#### `ProjectUtilsConventionPlugin`

**Essential developer tools and project information**

**Information Tasks (project-info group):**

- `printProjectStructure`: Dynamic project structure with namespace detection
- `showCoverageInfo`: Coverage commands and module breakdown
- `showTestInfo`: Test execution guidance with performance tips

**Features:**

- **Dynamic Module Detection**: Automatically adapts to project changes
- **Performance Guidance**: Notes about `--parallel`, `--configuration-cache`, `--rerun-tasks`

## Utility Functions

### 📁 Module Detection (`ProjectUtils.kt`)

**Dynamic module type detection based on build file content analysis:**

**Benefits:**
- **Future-Proof**: Automatically adapts to new modules
- **Consistent**: Single source of truth across all plugins
- **Excludes Test Modules**: Filters out `testing` and `build-logic` modules

### 📊 Enhanced Test Reporting (`TestUtils.kt`)

**Test result collection and professional reporting:**

**Key Functions:**

- `collectTestResults()`: Collects both unit and UI test results
- `printTestSummary()`: Professional formatting with module categorization
- `createClickableFileUrl()`: Cross-platform coverage report links

**Features:**

- **UI Test Detection**: Automatically identifies and separates UI test results
- **Module Categorization**: Groups by Android vs JVM modules
- **Success Rate Calculations**: Detailed statistics and visual indicators
- **Coverage Integration**: Seamless integration with Jacoco reports

## Performance Optimizations

### Configuration Cache Compatibility

All tasks and plugins support Gradle's configuration cache:

- **Path Capture**: All file paths captured at configuration time
- **No Project Serialization**: Avoids serializing Project instances in task execution
- **Improved Build Performance**: Faster subsequent builds

### Parallel Execution

- **Multi-Module Optimization**: Tasks designed for parallel execution
- **Default in gradle.properties**: Automatically enabled project-wide
- **Smart Dependencies**: Minimal task graph for optimal parallelization

### Pattern-Based File Collection

```kotlin
// Device-agnostic execution data collection
fileTree("$moduleDir/build/outputs/code_coverage/debugAndroidTest") {
    include("**/*.ec")
}
```

**Benefits:**

- **Device Independence**: Works with any Android device/emulator
- **Directory Variants**: Handles different Android test configurations
- **Graceful Degradation**: Missing directories don't break the build

## Architecture Benefits

### 🎯 Accuracy

- **Smart Filtering**: Only includes modules with relevant test sources
- **Realistic Metrics**: Coverage percentages reflect actual test scope
- **Clean Reports**: Generated code filtered out for clarity

### 🚀 Performance

- **Configuration Cache**: Optimal build performance
- **Parallel Execution**: Multi-module optimization
- **Minimal Dependencies**: Only depend on what exists

### 🛠️ Maintainability

- **Dynamic Detection**: No hardcoded module lists
- **Centralized Logic**: Single source of truth for module detection
- **Documentation**: Self-documenting code with clear explanations

### 📊 Developer Experience

- **Professional Summaries**: Clear, actionable test reports
- **Clickable Reports**: Easy access to coverage details
- **Performance Guidance**: Built-in optimization recommendations
- **Context-Aware Help**: Relevant commands and tips

---

## 🚀 Usage - Reusing Convention Plugins in New Projects

### **Quick Start Guide**

This build logic can be easily reused in other multi-module Android projects. Follow these steps:

#### **1. Copy Build Logic Directory**
```bash
# Copy the entire build-logic directory to your new project
cp -r /path/to/this/project/build-logic /path/to/your/new/project/
```

#### **2. Update Plugin Prefix (Automatic)**

The system will automatically detect your plugin prefix from `build-logic/convention/build.gradle.kts`. Simply update the plugin IDs:

```kotlin
// In build-logic/convention/build.gradle.kts
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "yourproject.android.application"  // Update this
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "yourproject.android.library"      // Update this
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        // ... update all other plugin IDs with your prefix
    }
}
```

#### **3. Update Settings File**

```kotlin
// In your project's settings.gradle.kts
pluginManagement {
    includeBuild("build-logic")
}
```

#### **4. Apply to Your Modules**

```kotlin
// Example: Android app module (app/build.gradle.kts)
plugins {
    id("yourproject.android.application")
    id("yourproject.android.application.compose")
    id("yourproject.android.hilt")
    id("yourproject.quality.detekt")
    id("yourproject.quality.spotless")
}

// Example: Android library module (core/data/build.gradle.kts)
plugins {
    id("yourproject.android.library")
    id("yourproject.android.hilt")
    id("yourproject.quality.detekt")
    id("yourproject.quality.spotless")
}

// Example: JVM library module (core/domain/build.gradle.kts)
plugins {
    id("yourproject.jvm.library")
    id("yourproject.quality.detekt")
    id("yourproject.quality.spotless")
}
```

#### **5. Apply Root Testing Plugin**

```kotlin
// In your root build.gradle.kts
plugins {
    id("yourproject.root.testing")
    id("yourproject.project.utils")
}
```

### **Advanced Configuration**

#### **Manual Plugin Prefix Override**

If you prefer manual control over the plugin prefix detection:

```kotlin
// In build-logic/convention/src/main/kotlin/com/mustalk/seat/marsrover/ProjectUtils.kt
private const val AUTO_DETECT_PREFIX = false    // Disable auto-detection
private const val FALLBACK_PREFIX = "yourproject"  // Set your prefix
```

#### **Customizing Exclusions**

Update coverage exclusions for your project-specific generated code:

```kotlin
// In build-logic/convention/src/main/kotlin/com/mustalk/seat/marsrover/CoverageExclusions.kt

// Add to staticCoverageExclusions list:
private val staticCoverageExclusions =
    listOf(
        // ... existing exclusions ...

        // Add your project-specific exclusions here:
        "**/YourCustomGenerated*.class",
        "**/YourSpecificPackage/**",
        "**/Custom*Navigation*.class",
    )
```

**Dynamic Application Class Detection:**
The system automatically detects and excludes:

- Application classes from `AndroidManifest.xml`: `android:name=".YourAppApplication"`
- Application classes from source code: `class YourAppApplication : Application()`
- Works with both Kotlin and Java
- Supports fully qualified class names and relative references

**Manual Override (if needed):**
If you need to disable dynamic detection:

```kotlin
// In createDynamicExclusions() function, return early:
private fun createDynamicExclusions(rootProjectDir: File): List<String> =
    emptyList() // Disable dynamic detection
```

#### **Gradle Properties Setup**

Add these to your `gradle.properties` for optimal performance:

```properties
# Enable parallel execution (recommended)
org.gradle.parallel=true

# Enable configuration cache (optional - can be tricky sometimes during development and debugging)
# org.gradle.configuration-cache=true

# Only configure projects that are needed for the current build
org.gradle.configureondemand=true

# JVM settings for better performance
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC
```

### **Modularization Strategy Compatibility**

This build logic is designed to work with **any modularization strategy**:

#### **🏗️ Hybrid / Multi-Layered Architecture (Core + Feature)**

```
project/
├── app/                              # Android application
├── core/
│   ├── model/                        # JVM library (shared models)
│   ├── domain/                       # JVM library (use cases)
│   ├── data/                         # Android library (repositories)
│   └── ui/                           # Android library (design system)
└── feature/
    ├── login/                        # Android library (feature)
    └── dashboard/                    # Android library (feature)
```

#### **🎯 Pure Feature-Based Architecture**

```
project/
├── app/                              # Android application (navigation only)
├── shared/
│   ├── common/                       # JVM library (utilities)
│   └── design/                       # Android library (UI components)
└── features/
    ├── user-management/              # Android library (complete feature)
    ├── product-catalog/              # Android library (complete feature)
    └── shopping-cart/                # Android library (complete feature)
```

#### **🧩 Clean Architecture + Domain-Driven Design**

```
project/
├── app/                              # Android application
├── domain/
│   ├── user/                         # JVM library (user domain)
│   ├── product/                      # JVM library (product domain)
│   └── order/                        # JVM library (order domain)
├── data/
│   ├── user-data/                    # Android library (user data layer)
│   ├── product-data/                 # Android library (product data layer)
│   └── order-data/                   # Android library (order data layer)
└── presentation/
    ├── user-ui/                      # Android library (user screens)
    ├── product-ui/                   # Android library (product screens)
    └── order-ui/                     # Android library (order screens)
```

#### **📱 Platform-Specific Modularization**

```
project/
├── androidApp/                       # Android application
├── shared/
│   ├── business/                     # JVM library (business logic)
│   ├── network/                      # JVM library (API clients)
│   └── database/                     # JVM library (data models)
└── android/
    ├── ui-components/                # Android library (shared UI)
    ├── feature-a/                    # Android library (feature)
    └── feature-b/                    # Android library (feature)
```

**Key Points:**

- **Module Detection**: Based on build file content, not naming conventions
- **Flexible Structure**: Works with any directory organization
- **Coverage Smart Categorization**: Automatically detects Android vs JVM modules
- **Adaptive Coverage**: Only includes modules with relevant test sources

> **Note**: While this build logic is designed to be architecture-agnostic and work with most modularization strategies out of the box, some edge
> cases or highly specialized architectures may require minor adaptations to the convention plugins. The system was developed and tested primarily with
> a hybrid multi-layered architecture, so teams using significantly different approaches should test thoroughly and may need to adjust plugin detection
> logic, coverage exclusions, or task dependencies to fit their specific needs.

### **Available Commands After Setup**

Once configured, you'll have access to all the enhanced testing and coverage commands:

```bash
# Testing commands
./gradlew testAllModules                    # Unit tests only
./gradlew testCompleteSuite                # Unit + UI tests
./gradlew testAllWithCoverage              # Unit tests + coverage
./gradlew testCompleteSuiteWithCoverage    # Everything + coverage

# Project information
./gradlew printProjectStructure            # View your project structure
./gradlew showCoverageInfo                 # Coverage commands for your project
./gradlew showTestInfo                     # Test execution guidance

# Cleanup
./gradlew cleanAll                         # Complete project cleanup
```

---

This system provides a **production-ready foundation** that can significantly accelerate the setup of new multi-module Android projects while ensuring
consistent quality and testing practices.
