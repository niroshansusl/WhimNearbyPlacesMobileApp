package com.niroshan.whimmobileappassignment.di
import com.niroshan.whimmobileappassignment.NearByPlacesApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, NetworkModule::class, RepositoryModule::class,
        ViewModelModule::class, ActivityModule::class]
)
interface AppComponent : AndroidInjector<NearByPlacesApplication>
