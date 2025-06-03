package com.mustalk.seat.marsrover.core.di

import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.core.domain.validator.InputValidator
import com.mustalk.seat.marsrover.domain.usecase.ExecuteRoverMissionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Hilt dependency injection module that configures how dependencies are provided and bound.
 *
 * @Module - Tells Hilt this class provides dependencies
 * @InstallIn(SingletonComponent::class) - Makes dependencies available app-wide as singletons
 *
 * Abstract class is required because @Binds methods must be abstract.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("UnnecessaryAbstractClass")
abstract class AppModule {
    /**
     * @Binds tells Hilt: "When someone asks for JsonParser interface, provide JsonParserImpl instance"
     * This is more efficient than @Provides for simple interface-to-implementation bindings.
     * Hilt will automatically call JsonParserImpl's @Inject constructor.
     * @Binds
     * abstract fun bindJsonParser(impl: JsonParserImpl): JsonParser
     */

    companion object {
        /**
         * @Provides creates actual instances with custom configuration.
         * @Singleton ensures only one Json instance exists app-wide.
         *
         * Used here because we need to configure Json with specific settings,
         * not just instantiate with default constructor.
         */
        @Provides
        @Singleton
        fun provideJson(): Json =
            Json {
                ignoreUnknownKeys = true // Don't fail on extra JSON fields
                prettyPrint = true // Format JSON nicely for debugging
                isLenient = true // Allow relaxed JSON parsing
            }

        /**
         * Provides ExecuteRoverMissionUseCase for injection.
         * This allows the use case to be injected in other components like interceptors.
         */
        @Provides
        @Singleton
        fun provideExecuteRoverMissionUseCase(
            jsonParser: JsonParser,
            inputValidator: InputValidator,
            roverMovementService: RoverMovementService,
        ): ExecuteRoverMissionUseCase =
            ExecuteRoverMissionUseCase(
                jsonParser = jsonParser,
                inputValidator = inputValidator,
                roverMovementService = roverMovementService
            )
    }
}
