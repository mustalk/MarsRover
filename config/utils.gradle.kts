// Install git hooks automatically.
gradle.taskGraph.whenReady {
    val from = File("${rootProject.rootDir}/config/pre-commit")
    val to = File("${rootProject.rootDir}/.git/hooks/pre-commit")
    from.copyTo(to, overwrite = true)
    to.setExecutable(true)
}

// gradle task to print project structure. usage: ./gradlew printPrettyProjectStructure
tasks.register("printPrettyProjectStructure") {
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

        val sourceSets =
            mapOf(
                "main" to file("src/main/kotlin"),
                "androidTest" to file("src/androidTest/kotlin"),
                "test" to file("src/test/kotlin")
            )

        sourceSets.forEach { (sourceSetName, sourceSetPath) ->
            println(sourceSetName)
            printDirectoryStructure(sourceSetPath)
        }
    }
}
