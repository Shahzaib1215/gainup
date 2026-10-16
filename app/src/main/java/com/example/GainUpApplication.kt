package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.GainUpRepository
import com.example.notification.GainUpNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GainUpApplication : Application() {

    lateinit var repository: GainUpRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = GainUpRepository(database.gainUpDao())

        // Create notification channels
        GainUpNotificationHelper.createNotificationChannel(this)

        // Preload initial database content in background
        CoroutineScope(Dispatchers.IO).launch {
            repository.checkAndSeedInitialData()
        }
    }
}
