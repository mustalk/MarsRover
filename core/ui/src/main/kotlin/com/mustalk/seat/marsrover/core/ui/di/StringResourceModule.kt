package com.mustalk.seat.marsrover.core.ui.di

import com.mustalk.seat.marsrover.core.ui.resource.StringResourceProvider
import com.mustalk.seat.marsrover.core.ui.resource.StringResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StringResourceModule {
    @Binds
    @Singleton
    abstract fun bindStringResourceProvider(stringResourceProviderImpl: StringResourceProviderImpl): StringResourceProvider
}
