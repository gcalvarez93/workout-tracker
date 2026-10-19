// Path: app/src/main/java/com/castrodev/workouttracker/WorkoutTrackerApp.kt
package com.castrodev.workouttracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WorkoutTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}