# Mars Rover Challenge - Android Implementation

[![Android CI/CD](https://github.com/mustalk/MarsRover/actions/workflows/android-ci-cd.yml/badge.svg)](https://github.com/mustalk/MarsRover/actions/workflows/android-ci-cd.yml)

This repository contains the Android application developed for the Mars Rover coding challenge. The application allows users to define a plateau on
Mars, specify a rover's starting position and direction, and send a series of movement instructions. The app then calculates and displays the rover's
final position.

### App Demo

<div align="center">
  <img src="screenshots/mars-rover-demo.gif" alt="Mars Rover App Demo" width="300"/>
</div>
<br/>
<div align="center">

**[⬇️ Download Latest Debug APK](https://github.com/mustalk/MarsRover/releases/latest/download/mustalk-mars-rover-debug.apk)**

<small><em>Note: This is a debug build. You may need to enable "Install from unknown sources" on your device.</em></small>

</div>

---

## How to Launch and Build the Project

### Prerequisites

* **Android Studio:** Android Studio Meerkat | 2024.3.2 or later is recommended.
    * *Initial attempts with Android Studio Ladybug | 2024.2.2 Patch 1 encountered AGP compatibility issues (e.g., "AGP 8.10.0 is incompatible, latest
      supported is AGP 8.8.1"). Upgrading to a newer Android Studio version (like Meerkat 2024.3.2) resolved these compilation problems.*
* **Android Gradle Plugin (AGP):** The project is configured to use AGP version 8.10.0 (as defined in `gradle/lib.versions.toml`). Ensure your Android
  Studio version supports this AGP version (see [Android Studio & AGP compatibility](https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility)).
* **Java Development Kit (JDK):** JDK 11.
* **Android SDK:** API level 33 or higher installed for compilation, though the app targets `minSdk = 24`.
* **Emulator/Device:** An Android Emulator or a physical Android device (API 24+).

### Steps to Build and Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/mustalk/MarsRover.git
   cd MarsRover
   ```
2. **Open in Android Studio:**
    * Launch Android Studio.
    * Select "Open an existing Android Studio project".
    * Navigate to the cloned `MarsRover` directory and open it.
3. **Sync Gradle:**
    * Android Studio should automatically sync the Gradle project. If not, click "Sync Project with Gradle Files" (elephant icon in the toolbar).
4. **Build the Project:**
    * From the menu, select `Build` > `Assemble Project` (or Ctrl+F9 / Cmd+F9).
    * Alternatively, to build a debug APK from the command line:
      ```bash
      ./gradlew assembleDebug
      ```
5. **Run the Application:**
    * Select an emulator or connect a physical device.
    * Click the "Run 'app'" button (green play icon) in Android Studio or select `Run` > `Run 'app'` (Shift+F10).

## Architectural Decisions & Design Choices

This project adheres to modern Android development best practices, focusing on a scalable, maintainable, and testable codebase.

* **Architecture:** MVVM (Model-View-ViewModel) pattern is used for the presentation layer, separating UI logic from business logic.
    * **View (Compose UI):** Jetpack Compose is used for building the user interface declaratively.
    * **ViewModel:** Manages UI-related state using Kotlin Coroutines and StateFlow, handles user events, and delegates business logic to UseCases.
    * **Model (Domain + Data):**
        * **UseCases (Domain Layer):** Encapsulate specific business logic operations (e.g., `ExecuteRoverMissionUseCase`,
          `ExecuteNetworkMissionUseCase`). Pure Kotlin modules.
        * **Repositories (Data Layer Interface):** Abstract data sources. For this project, data sources include local JSON processing and a simulated
          network API.
        * **Data Models (Domain/Data):** Clearly defined data classes for domain concepts (`Plateau`, `Position`, `Direction`, `Rover`) and DTOs for
          data transfer (`MarsRoverInput`, `MissionResponse`).
* **Clean Architecture Principles:** The project structure follows principles of Clean Architecture, with a clear separation of concerns between
  layers (presentation, domain, data). This promotes modularity and testability.
* **SOLID Principles:** Applied throughout the codebase to ensure maintainability and flexibility.
* **Dependency Injection:** Hilt is used for managing dependencies, simplifying the creation and provision of objects throughout the application,
  including ViewModels, UseCases, and Repositories.
* **Kotlin Coroutines & Flows:** Asynchronous operations are handled using Kotlin Coroutines for managing background threads efficiently and StateFlow
  for reactive UI state updates.
* **Functional Programming Patterns:** Where practical, functional programming concepts like sealed classes for representing state (e.g.,
  `NetworkResult` for API calls) were employed to enhance type safety, error handling, and code expressiveness.
* **Modularity:** The codebase is structured with future modularization in mind (e.g., separating domain and data layers into their own Gradle modules
  if the project were to scale).
* **Immutability:** Favoring immutable data structures (`val`, `data class`) where practical, especially for UI state and domain models.

## Key Features

* **Rover Simulation Logic:** Core domain logic to parse instructions (`L`, `R`, `M`), turn the rover, move it on a plateau, and handle boundary
  conditions.
* **Dual Input Modes on New Mission Screen:**
    * **JSON Input:** Allows users to provide mission parameters as a raw JSON string.
    * **Builder Mode:** A user-friendly form to input plateau dimensions, rover start position/direction, and movement commands field by field.
* **API Simulation & Functional Error Handling:**
    * To demonstrate capabilities in handling network operations and API integration, the "Builder" mode simulates network API calls for mission
      execution.
    * This is achieved using OkHttp and Retrofit with a custom `Interceptor` (`MissionSimulationInterceptor`). This interceptor reuses the
      core domain logic by invoking the `ExecuteRoverMissionUseCase` internally. This ensures data processing consistency whether the mission
      originates from a local JSON or a simulated network request, avoiding duplication of business logic.
    * The simulation includes realistic network delays and response structures (success/error).
    * **Functional Programming for Network Results:** Network call outcomes are wrapped in a `NetworkResult<T>` sealed class. This monad-like
      structure provides a robust and functional way to handle success, error, and loading states, improving the clarity and reliability of
      asynchronous operations and error propagation (e.g., using `map`, `flatMap`, `fold`). This decision was made to showcase the modern approach to
      handling asynchronous data flows.
* **User Interface (Jetpack Compose):**
    * **Dashboard Screen:** Displays results of previous missions and allows navigation to create new missions.
    * **New Mission Screen:** Provides JSON and Builder input modes.
    * **Theming:** Custom Mars-themed design (light and dark modes) with reusable UI components.
    * **Animations:** Lottie animations for loading states and slide animations for screen transitions.
* **Error Handling:** Comprehensive error handling for invalid inputs, JSON parsing errors, and (simulated) network issues, with user-friendly toast
  messages.
* **State Management:** UI state managed in ViewModels using `StateFlow`, collected reactively by Composable screens.

## Testing Strategy

The project emphasizes on a robust testing strategy, with 180+ total tests:

* **Unit Tests:**
    * Located in `app/src/test/kotlin`.
    * Cover domain logic (UseCases, services, validators, parsers), ViewModels, and utility classes like `NetworkResult`.
    * Mockito/MockK for mocking dependencies.
    * JUnit and Truth/AssertJ for assertions.
* **UI Tests (Jetpack Compose):**
    * Located in `app/src/androidTest/kotlin`.
    * Test Composable screens, individual UI components, and navigation flows.
    * `MarsRoverNavigationTest` specifically tests navigation flows and screen interactions.
* **Hilt for Testing:**
    * `HiltTestRunner` and `@HiltAndroidTest` are configured to enable dependency injection in UI tests, allowing for more realistic test scenarios.

## Code Quality Tools

The project integrates the following tools to maintain code quality and consistency:

* **KtLint:** For Kotlin code style checking and formatting.
* **Spotless:** For applying formatting rules across various file types, configured via Gradle.
* **Detekt:** For static code analysis to identify potential code smells and anti-patterns.
* **Pre-commit Hook:** A Git pre-commit hook (located in `config/pre-commit` and automatically installed via `config/utils.gradle.kts`) is configured
  to run Spotless and Detekt checks before any commit. This helps ensure that no code failing quality checks is committed to the repository, enforcing
  standards directly within the development workflow.

These checks are integrated into the CI pipeline to ensure code quality before merging.

## CI/CD (GitHub Actions)

The project utilizes GitHub Actions for an integrated Continuous Integration and Continuous Deployment (CI/CD) pipeline, managed within the
`android-ci-cd.yml` workflow.

* **CI Workflow (within `android-ci-cd.yml`):**
    * **Triggers:** On push to `main` and feature branches (`feat/**`, `chore/**`, etc.).
    * **Concurrency Control:** Automatically cancels previous runs for the same branch/PR to save resources.
    * **Setup:** Standard code checkout, JDK, and Gradle setup with optimized caching (read-only for feature branches, write for `main`).
    * **Static Analysis:** Runs Detekt and KtLint in parallel for code quality and style checks.
    * **Unit Tests:** Executes all unit tests (`./gradlew test`).
    * **Build:** Compiles a debug APK (`./gradlew assembleDebug`).
    * **UI Tests:**
        * Sets up an Android emulator with memory optimizations and resource monitoring.
        * Runs instrumented UI tests (`./gradlew connectedCheck`).
    * **Artifacts:** Uploads test reports and the debug APK.

* **CD Workflow (via reusable GitHub Action triggered by `android-ci-cd.yml`):**
    * **Trigger:** Implicitly triggered after a successful CI run on a merged PR to `main` (as defined by job dependencies and conditions within
      `android-ci-cd.yml`).
    * **Reusable composite action (`.github/actions/execute/android-deploy/action.yml`):**
        * Encapsulates the deployment logic, called by the main `android-deploy` job in `android-ci-cd.yml`.
        * Handles version extraction from `build.gradle.kts`.
        * Manages Git tagging (creates new or recreates existing tags based on `versionName`).
        * Generates release notes including version, commit SHA, and link to the last merged PR.
        * Creates or updates a GitHub Release, replacing existing APK assets with the new debug APK.
    * **Artifact Naming:** Uses dynamic artifact naming for better tracking.

* **Key Optimizations & Features in the Pipeline:**
    * Optimized Gradle cache strategy.
    * Emulator memory optimization and resource monitoring during UI tests.
    * Automated Git tagging and comprehensive GitHub Release management.

* **Important Note:** The current CD process distributes debug APKs and is primarily intended for testing, demonstration, and internal review. For
  production releases, a more robust strategy involving release builds, code signing, and potentially direct store distribution (e.g., Google Play)
  would be necessary.

This integrated CI/CD pipeline ensures code quality, automates testing, and provides a streamlined way to generate and distribute debug builds of the
application.

## Considerations & Future Improvements

* **Production CD:** The current CD pipeline is basic and focuses on debug builds. For production, it would need enhancements like release build
  variants, code signing, Play Store deployment integration, an appropriate release note generation, etc...
* **UI Test Hilt Setup:** While Hilt is configured for UI tests (e.g., `MarsRoverNavigationTest`), further refactoring could be done to leverage Hilt
  more extensively across all UI test classes if they require ViewModel injection.
* **Advanced Error Handling:** Implement more granular error states in the UI beyond simple toasts for certain scenarios.
* **Data Persistence:** While not strictly required by the challenge for the core logic, persisting the last mission input/output locally (e.g., using
  DataStore or Room) could enhance user experience on app restart.
* **Multi-Module Architecture:** The project is designed with modularity in mind. For larger applications, the `domain` and `data` layers could be
  extracted into separate Gradle library modules.

---

This README provides an overview of the Mars Rover Android challenge solution. For specific code details, please refer to the source files and their
accompanying KDoc comments.

## Code Challenge Version

**Version: 1**
