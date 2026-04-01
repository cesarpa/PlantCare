package com.cesarpa.plantcare.data.local

import androidx.room.TypeConverter
import com.cesarpa.plantcare.data.model.EnergySource
import com.cesarpa.plantcare.data.model.PotType
import com.cesarpa.plantcare.data.model.SoilType

class Converters {
    @TypeConverter
    fun fromEnergySource(value: EnergySource) = value.name

    @TypeConverter
    fun toEnergySource(value: String) = EnergySource.valueOf(value)

    @TypeConverter
    fun fromPotType(value: PotType) = value.name

    @TypeConverter
    fun toPotType(value: String) = PotType.valueOf(value)

    @TypeConverter
    fun fromSoilType(value: SoilType) = value.name

    @TypeConverter
    fun toSoilType(value: String) = SoilType.valueOf(value)
}
