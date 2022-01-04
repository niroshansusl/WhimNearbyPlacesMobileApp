package com.niroshan.whimmobileappassignment.di

import com.niroshan.whimmobileappassignment.view.NearByPlacesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule{
    @ContributesAndroidInjector
    abstract fun contributeMapsActivity(): NearByPlacesActivity
}