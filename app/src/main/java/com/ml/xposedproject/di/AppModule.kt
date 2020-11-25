package com.ml.xposedproject.di

import android.app.Application
import com.ml.xposedproject.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 17:47
 * Description: This is AppModule
 * Package: com.ml.xposedproject.di
 * Project: XposedProject
 */
@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    fun application(application:Application):App{
        return application as App
    }

}