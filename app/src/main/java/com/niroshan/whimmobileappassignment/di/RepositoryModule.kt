package com.niroshan.whimmobileappassignment.di

import com.niroshan.whimmobileappassignment.model.repository.RestaurantsApiService
import com.niroshan.whimmobileappassignment.model.repository.RestaurantsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): RestaurantsApiService {
        return retrofit.create(RestaurantsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideLocalRepository(restaurantsApiService: RestaurantsApiService) =
        RestaurantsRepository(
            restaurantsApiService
        )

    @Named("secret_key")
    @Singleton
    @Provides
    fun provideSecretKey() = "API-KEY"

}
