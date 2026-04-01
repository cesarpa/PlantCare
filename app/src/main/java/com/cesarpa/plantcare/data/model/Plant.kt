package com.cesarpa.plantcare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EnergySource {
    DIRECT_SUN, PARTIAL_SHADE, FULL_SHADE
}

enum class PotType {
    CLAY, PLASTIC, CERAMIC, SELF_WATERING
}

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val soilType: String,
    val potType: PotType,
    val energySource: EnergySource,
    val waterInterval: Int, // In days
    val lastWatered: Long   // Epoch timestamp
)