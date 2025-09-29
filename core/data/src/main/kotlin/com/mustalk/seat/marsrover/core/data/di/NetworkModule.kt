package com.mustalk.seat.marsrover.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.core.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.core.data.network.interceptor.MissionSimulationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt dependency injection module for network-related dependencies.
 *
 * Provides OkHttp, Retrofit, API services, and repositories with proper configuration
 * for Mars rover network operations.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("TooManyFunctions")
object NetworkModule {
    /**
     * Provides an HTTP logging interceptor for debugging network requests.
     *
     * @return Configured HttpLoggingInterceptor
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * Provides a configured OkHttpClient with interceptors and timeouts.
     *
     * @param loggingInterceptor HTTP logging interceptor for debugging
     * @param missionSimulationInterceptor Mission simulation interceptor for API mocking
     * @return Configured OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        missionSimulationInterceptor: MissionSimulationInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(missionSimulationInterceptor) // Add simulation first
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    /**
     * Provides a configured Retrofit instance for Mars rover API.
     *
     * @param okHttpClient Configured OkHttpClient
     * @param json Json instance for serialization (provided by DataModule)
     * @return Configured Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(Constants.Network.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    /**
     * Provides the Mars rover API service interface implementation.
     *
     * @param retrofit Configured Retrofit instance
     * @return MarsRoverApiService implementation
     */
    @Provides
    @Singleton
    fun provideMarsRoverApiService(retrofit: Retrofit): MarsRoverApiService = retrofit.create(MarsRoverApiService::class.java)
}
