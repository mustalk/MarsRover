package com.mustalk.seat.marsrover.di

import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import com.mustalk.seat.marsrover.core.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteNetworkMissionUseCase
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteRoverMissionUseCase
import com.mustalk.seat.marsrover.core.domain.validator.InputValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module that configures how dependencies are provided and bound.
 *
 * @Module - Tells Hilt this class provides dependencies
 * @InstallIn(SingletonComponent::class) - Makes dependencies available app-wide as singletons
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides InputValidator implementation.
     * Uses @Provides because InputValidatorImpl is a pure Kotlin class without @Inject constructor.
     */
    @Provides
    @Singleton
    fun provideInputValidator(): InputValidator =
        com.mustalk.seat.marsrover.core.domain.validator
            .InputValidatorImpl()

    /**
     * Provides RoverMovementService implementation.
     * Uses @Provides because RoverMovementServiceImpl is a pure Kotlin class without @Inject constructor.
     */
    @Provides
    @Singleton
    fun provideRoverMovementService(): RoverMovementService =
        com.mustalk.seat.marsrover.core.domain.service
            .RoverMovementServiceImpl()

    /**
     * Provides ExecuteRoverMissionUseCase for injection.
     * This allows the use case to be injected in other components like interceptors.
     *
     * Note: JsonParser is provided by DataModule in core:data
     * Note: ExecuteRoverMissionUseCase is now in core:domain (pure domain logic)
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

    /**
     * Provides ExecuteNetworkMissionUseCase for injection.
     * This allows the use case to be injected in ViewModels.
     * Note: JsonParser and MarsRoverRepository are provided by DataModule in core:data
     */
    @Provides
    @Singleton
    fun provideExecuteNetworkMissionUseCase(
        repository: MarsRoverRepository,
        jsonParser: JsonParser,
    ): ExecuteNetworkMissionUseCase =
        ExecuteNetworkMissionUseCase(
            repository = repository,
            jsonParser = jsonParser
        )
}
