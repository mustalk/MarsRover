package com.mustalk.seat.marsrover

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Extension to access the version catalog in convention plugins.
 * Eliminates the need to duplicate this code in every plugin.
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Extension to get common dependency versions from the catalog.
 */
fun VersionCatalog.version(alias: String): String = findVersion(alias).get().toString()

/**
 * Extension to safely get library references from the catalog.
 */
fun VersionCatalog.library(alias: String) = findLibrary(alias).get()

fun VersionCatalog.bundle(alias: String) = findBundle(alias).get()
