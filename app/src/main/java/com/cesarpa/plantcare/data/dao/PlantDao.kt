package com.cesarpa.plantcare.data.dao

import androidx.room.*
import com.cesarpa.plantcare.data.model.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY name ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Long): Plant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("UPDATE plants SET lastWatered = :timestamp WHERE id = :id")
    suspend fun updateLastWatered(id: Long, timestamp: Long)
}
