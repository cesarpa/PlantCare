package com.cesarpa.plantcare

import android.app.Application
import com.cesarpa.plantcare.data.local.PlantDatabase
import com.cesarpa.plantcare.data.repository.PlantRepository
import com.cesarpa.plantcare.worker.WateringWorker

class PlantCareApplication : Application() {
    val database by lazy { PlantDatabase.getDatabase(this) }
    val repository by lazy { PlantRepository(database.plantDao()) }

    override fun onCreate() {
        super.onCreate()
        WateringWorker.enqueue(this)
    }
}
