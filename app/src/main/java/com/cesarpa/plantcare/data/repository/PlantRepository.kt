package com.cesarpa.plantcare.data.repository

import com.cesarpa.plantcare.data.dao.PlantDao
import com.cesarpa.plantcare.data.model.Plant
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants()

    suspend fun getPlantById(id: Long): Plant? {
        return plantDao.getPlantById(id)
    }

    suspend fun insertPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(plant)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant)
    }

    suspend fun updateLastWatered(id: Long, timestamp: Long) {
        plantDao.updateLastWatered(id, timestamp)
    }
}
