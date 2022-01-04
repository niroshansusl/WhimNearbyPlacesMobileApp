package com.niroshan.whimmobileappassignment

import android.app.Application
import com.niroshan.whimmobileappassignment.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class NearByPlacesApplication : Application(), HasAndroidInjector {
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create().inject(this)
    }
    @Inject lateinit var androidInjector : DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}