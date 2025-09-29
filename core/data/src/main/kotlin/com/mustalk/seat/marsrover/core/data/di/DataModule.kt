package com.mustalk.seat.marsrover.core.data.di

import com.mustalk.seat.marsrover.core.data.parser.JsonParserImpl
import com.mustalk.seat.marsrover.core.data.repository.MarsRoverRepositoryImpl
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Hilt module for binding data layer implementations to domain interfaces
 * and providing data-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    /**
     * Binds JsonParserImpl to JsonParser interface.
     */
    @Binds
    abstract fun bindJsonParser(impl: JsonParserImpl): JsonParser

    /**
     * Binds MarsRoverRepositoryImpl to MarsRoverRepository interface.
     */
    @Binds
    abstract fun bindMarsRoverRepository(impl: MarsRoverRepositoryImpl): MarsRoverRepository

    companion object {
        /**
         * Provides Json instance with configuration for data serialization/deserialization.
         *
         * Used throughout the data layer for JSON parsing and generation.
         * Configuration supports lenient parsing and pretty printing for debugging.
         *
         * @return Configured Json instance
         */
        @Provides
        @Singleton
        fun provideJson(): Json =
            Json {
                ignoreUnknownKeys = true // Don't fail on extra JSON fields
                prettyPrint = true // Format JSON nicely for debugging
                isLenient = true // Allow relaxed JSON parsing
            }
    }
}
