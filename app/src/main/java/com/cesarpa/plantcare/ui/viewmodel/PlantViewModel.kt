package com.cesarpa.plantcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cesarpa.plantcare.data.model.Plant
import com.cesarpa.plantcare.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {

    val allPlants: StateFlow<List<Plant>> = repository.allPlants
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(plant: Plant) = viewModelScope.launch {
        repository.insertPlant(plant)
    }

    fun update(plant: Plant) = viewModelScope.launch {
        repository.updatePlant(plant)
    }

    fun delete(plant: Plant) = viewModelScope.launch {
        repository.deletePlant(plant)
    }

    fun waterPlant(plant: Plant) = viewModelScope.launch {
        val currentTime = System.currentTimeMillis()
        repository.updateLastWatered(plant.id, currentTime)
    }

    suspend fun getPlantById(id: Long): Plant? {
        return repository.getPlantById(id)
    }
}

class PlantViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
