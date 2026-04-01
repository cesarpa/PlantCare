package com.cesarpa.plantcare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EnergySource {
    DIRECT_SUN, PARTIAL_SHADE, FULL_SHADE
}

enum class PotType {
    CLAY, PLASTIC, CERAMIC, SELF_WATERING
}

enum class SoilType {
    SANDY, LOAMY, CLAYEY, PEATY, SILTY
}

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val soilType: SoilType,
    val potType: PotType,
    val energySource: EnergySource,
    val waterInterval: Int, // In minutes (changed for testing granularity)
    val lastWatered: Long,   // Epoch timestamp
    val alreadyWatered: Boolean = true, // Flag to indicate if the plant was watered recently
    val imageUri: String? = null // URI or URL for the plant image
    )