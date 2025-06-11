import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Convention plugin for app-level utilities and configurations.
 * Handles git hooks setup and provides utility tasks.
 */
class AppUtilsConventionPlugin : Plugin<Project> {
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
        tasks.register("printPrettyProjectStructure") {
            group = "help"
            description = "Print the project structure in a pretty format for all modules"

            doLast {
                fun printDirectoryStructure(
                    dir: File,
                    prefix: String = "",
                    isLast: Boolean = true,
                ) {
                    if (dir.isDirectory) {
                        val children =
                            dir
                                .listFiles()
                                ?.filter { it.isDirectory || it.name.endsWith(".kt") || it.name.endsWith(".java") }
                                ?.sorted()
                        children?.forEachIndexed { index, file ->
                            val isChildLast = index == children.size - 1
                            val newPrefix = if (isLast) "$prefix    " else "$prefix|   "
                            println("$prefix${if (isChildLast) "|__ " else "|__ "}${file.name}")
                            printDirectoryStructure(file, newPrefix, isChildLast)
                        }
                    }
                }

                fun printModuleStructure(
                    projectPath: String,
                    projectDir: File,
                ) {
                    val sourceSets =
                        mapOf(
                            "main" to File(projectDir, "src/main/kotlin"),
                            "androidTest" to File(projectDir, "src/androidTest/kotlin"),
                            "test" to File(projectDir, "src/test/kotlin")
                        )

                    var hasContent = false
                    sourceSets.forEach { (sourceSetName, sourceSetPath) ->
                        if (sourceSetPath.exists()) {
                            if (!hasContent) {
                                println("\n=== $projectPath ===")
                                hasContent = true
                            }
                            println(sourceSetName)
                            printDirectoryStructure(sourceSetPath)
                        }
                    }
                }

                println("Project Structure:")
                println("==================")

                // Get project information without capturing Project references
                val rootProjectDir = project.rootProject.projectDir
                val allProjectDirs = mutableListOf<Pair<String, File>>()

                // Add root project
                allProjectDirs.add(":" to rootProjectDir)

                // Add all subprojects
                project.rootProject.subprojects.forEach { subproject ->
                    allProjectDirs.add(subproject.path to subproject.projectDir)
                }

                // Sort and print
                allProjectDirs.sortedBy { it.first }.forEach { (projectPath, projectDir) ->
                    printModuleStructure(projectPath, projectDir)
                }
            }
        }
    }
}
