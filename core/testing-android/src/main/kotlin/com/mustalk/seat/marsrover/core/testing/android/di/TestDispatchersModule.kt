package com.mustalk.seat.marsrover.core.testing.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

/**
 * Hilt test module that provides test dispatchers for all coroutines during testing.
 *
 * This module provides test dispatchers that can be injected into ViewModels, Use Cases,
 * and other components during testing to ensure predictable coroutine execution.
 *
 * When production dispatcher modules are added to the project, this module should be
 * updated to use `@TestInstallIn(replaces = [ProductionDispatcherModule::class])` to
 * replace the production dispatchers during testing.
 *
 * Offers:
 * - Consistent and predictable test execution
 * - Eliminates timing issues in tests
 * - Better test performance (no real threading overhead)
 * - Easier debugging with deterministic execution
 * - Works seamlessly with MainDispatcherRule
 *
 * Usage:
 * Simply annotate your test class with `@HiltAndroidTest` and this module will
 * automatically provide test dispatchers for all dependency injection.
 *
 * Example:
 * ```kotlin
 * @HiltAndroidTest
 * class MyViewModelTest {
 *     @get:Rule
 *     val hiltRule = HiltAndroidRule(this)
 *
 *     @get:Rule
 *     val mainDispatcherRule = MainDispatcherRule()
 *
 *     @Inject
 *     lateinit var viewModel: MyViewModel // Will receive test dispatchers
 * }
 * ```
 *
 * Future Enhancement:
 * When production dispatcher modules are added, update to:
 * ```kotlin
 * @TestInstallIn(
 *     components = [SingletonComponent::class],
 *     replaces = [ProductionDispatcherModule::class]
 * )
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
object TestDispatchersModule {
    /**
     * Provides a test dispatcher for all coroutines during testing.
     * Uses UnconfinedTestDispatcher for immediate execution.
     */
    @Provides
    @Singleton
    fun provideTestDispatcher(): TestDispatcher = UnconfinedTestDispatcher()

    /**
     * Provides a coroutine dispatcher that delegates to the test dispatcher.
     * This can be used anywhere a CoroutineDispatcher is needed in the DI graph.
     */
    @Provides
    @Singleton
    fun provideCoroutineDispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher
}
