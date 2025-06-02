# Mars Rover - Convention Plugins

This directory contains Gradle convention plugins for the Mars Rover project, implementing a modern Android build setup with consistent quality tools
across all modules.

## Available Convention Plugins

### Core Plugins

- **`marsrover.android.application`** - Android application module setup
- **`marsrover.android.library`** - Android library module setup
- **`marsrover.kotlin.library`** - Pure Kotlin library module setup

### Feature Plugins

- **`marsrover.android.compose`** - Jetpack Compose configuration (Note: Currently integrated into android.application)
- **`marsrover.android.hilt`** - Dagger Hilt dependency injection setup

### Quality Plugins

- **`marsrover.quality.detekt`** - Static code analysis with Detekt
- **`marsrover.quality.spotless`** - Code formatting with Spotless

## Usage

Apply convention plugins in your module's `build.gradle.kts`:

```kotlin
plugins {
    id("marsrover.android.application") // For app module
    id("marsrover.android.hilt")        // For Hilt DI
    id("marsrover.quality.detekt")      // For code analysis
    id("marsrover.quality.spotless")    // For formatting
}
```

## Benefits

- **Consistency**: All modules use the same configuration
- **Performance**: Configuration caching enabled
- **Maintainability**: Single source of truth for build configuration
- **Quality**: Unified code quality tools across the project

## Configuration

- **Java Version**: 17 (for compatibility)
- **Kotlin**: Latest stable version (from version catalog)
- **Android**: Compile SDK 35, Min SDK 24, Target SDK 35
- **Quality Tools**: Latest stable versions with Android-specific rules
