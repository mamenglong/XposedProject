package com.ml.xposedproject.di

import android.app.Application
import com.ml.xposedproject.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 17:47
 * Description: This is AppModule
 * Package: com.ml.xposedproject.di
 * Project: XposedProject
 */
/**
 * SingletonComponent	Application
ViewModelComponent	ViewModel
ActivityComponent	Activity
FragmentComponent	Fragment
ViewComponent	View
ViewWithFragmentComponent	View with @WithFragmentBindings
ServiceComponent	Service

 */
//https://dagger.dev/hilt/components.html#hilt-components

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun application(application:Application):App{
        return application as App
    }

}