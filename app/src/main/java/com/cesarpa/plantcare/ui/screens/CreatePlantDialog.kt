package com.cesarpa.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cesarpa.plantcare.data.model.EnergySource
import com.cesarpa.plantcare.data.model.Plant
import com.cesarpa.plantcare.data.model.PotType
import com.cesarpa.plantcare.data.model.SoilType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (Plant) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedSoilType by remember { mutableStateOf(SoilType.LOAMY) }
    
    // Interval Inputs
    var intervalDays by remember { mutableStateOf("0") }
    var intervalHours by remember { mutableStateOf("0") }
    var intervalMinutes by remember { mutableStateOf("10") } // Default 10 mins for testing
    
    // Last Watered Inputs
    var agoDays by remember { mutableStateOf("0") }
    var agoHours by remember { mutableStateOf("0") }
    var agoMinutes by remember { mutableStateOf("0") }
    
    var selectedPotType by remember { mutableStateOf(PotType.PLASTIC) }
    var selectedEnergySource by remember { mutableStateOf(EnergySource.PARTIAL_SHADE) }

    var soilTypeExpanded by remember { mutableStateOf(false) }
    var potTypeExpanded by remember { mutableStateOf(false) }
    var energySourceExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Planta 🌿") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Frecuencia de Riego:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = intervalDays,
                        onValueChange = { if (it.all { c -> c.isDigit() }) intervalDays = it },
                        label = { Text("Días") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = intervalHours,
                        onValueChange = { if (it.all { c -> c.isDigit() }) intervalHours = it },
                        label = { Text("Horas") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = intervalMinutes,
                        onValueChange = { if (it.all { c -> c.isDigit() }) intervalMinutes = it },
                        label = { Text("Mins") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dropdowns
                ExposedDropdownMenuBox(
                    expanded = soilTypeExpanded,
                    onExpandedChange = { soilTypeExpanded = !soilTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSoilType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Sustrato") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilTypeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = soilTypeExpanded, onDismissRequest = { soilTypeExpanded = false }) {
                        SoilType.entries.forEach { type ->
                            DropdownMenuItem(text = { Text(type.name) }, onClick = { selectedSoilType = type; soilTypeExpanded = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = potTypeExpanded,
                    onExpandedChange = { potTypeExpanded = !potTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPotType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Maceta") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = potTypeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = potTypeExpanded, onDismissRequest = { potTypeExpanded = false }) {
                        PotType.entries.forEach { type ->
                            DropdownMenuItem(text = { Text(type.name) }, onClick = { selectedPotType = type; potTypeExpanded = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = energySourceExpanded,
                    onExpandedChange = { energySourceExpanded = !energySourceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEnergySource.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Luz") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = energySourceExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = energySourceExpanded, onDismissRequest = { energySourceExpanded = false }) {
                        EnergySource.entries.forEach { source ->
                            DropdownMenuItem(text = { Text(source.name) }, onClick = { selectedEnergySource = source; energySourceExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        // Calc Total Interval in Minutes
                        val totalIntervalMins = (intervalDays.toIntOrNull() ?: 0) * 1440 +
                                               (intervalHours.toIntOrNull() ?: 0) * 60 +
                                               (intervalMinutes.toIntOrNull() ?: 0)
                        
                        // Calc Last Watered Timestamp
                        val totalAgoMins = (agoDays.toLongOrNull() ?: 0L) * 1440 +
                                          (agoHours.toLongOrNull() ?: 0L) * 60 +
                                          (agoMinutes.toLongOrNull() ?: 0L)
                        val lastWateredTime = System.currentTimeMillis() - (totalAgoMins * 60000L)
                        
                        // Literal condition from user: (currentDate - waterInterval) > lastWatered
                        val alreadyWateredStatus = (System.currentTimeMillis() - (totalIntervalMins * 60000L)) > lastWateredTime

                        onConfirm(
                            Plant(
                                name = name,
                                soilType = selectedSoilType,
                                potType = selectedPotType,
                                energySource = selectedEnergySource,
                                waterInterval = if (totalIntervalMins > 0) totalIntervalMins else 1,
                                lastWatered = lastWateredTime,
                                alreadyWatered = alreadyWateredStatus
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
