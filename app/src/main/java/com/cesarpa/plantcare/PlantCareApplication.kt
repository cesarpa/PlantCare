package com.cesarpa.plantcare

import android.app.Application
import com.cesarpa.plantcare.data.local.PlantDatabase
import com.cesarpa.plantcare.data.repository.PlantRepository

class PlantCareApplication : Application() {
    val database by lazy { PlantDatabase.getDatabase(this) }
    val repository by lazy { PlantRepository(database.plantDao()) }
}
