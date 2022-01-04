package com.niroshan.whimmobileappassignment.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.niroshan.whimmobileappassignment.di.viewmodel.ViewModelFactory
import com.niroshan.whimmobileappassignment.di.viewmodel.ViewModelKey
import com.niroshan.whimmobileappassignment.viewmodel.NearByPlacesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NearByPlacesViewModel::class)
    abstract fun bindsPickUpLocationsViewModel(yourViewModel: NearByPlacesViewModel): ViewModel

    // Factory
    @Binds
    abstract fun bindViewModelFactory(vmFactory: ViewModelFactory): ViewModelProvider.Factory
}