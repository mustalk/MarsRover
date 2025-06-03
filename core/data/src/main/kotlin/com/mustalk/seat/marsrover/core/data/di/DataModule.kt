package com.mustalk.seat.marsrover.core.data.di

import com.mustalk.seat.marsrover.core.data.parser.JsonParserImpl
import com.mustalk.seat.marsrover.core.data.repository.MarsRoverRepositoryImpl
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for binding data layer implementations to domain interfaces.
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
}
