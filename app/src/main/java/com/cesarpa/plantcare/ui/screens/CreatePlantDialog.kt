package com.cesarpa.plantcare.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.cesarpa.plantcare.data.model.EnergySource
import com.cesarpa.plantcare.data.model.Plant
import com.cesarpa.plantcare.data.model.PotType
import com.cesarpa.plantcare.data.model.SoilType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (Plant) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
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

    // Camera Logic
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                capturedImageUri = tempUri.value
                imageUri = capturedImageUri.toString()
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempImageUri(context)
            tempUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

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

                // Image Selection Section
                Text("Imagen de la Planta:", style = MaterialTheme.typography.labelLarge)
                
                if (capturedImageUri != null) {
                    AsyncImage(
                        model = capturedImageUri,
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                val uri = createTempImageUri(context)
                                tempUri.value = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tomar Foto 📸")
                    }
                }

                OutlinedTextField(
                    value = imageUri,
                    onValueChange = { 
                        imageUri = it
                        capturedImageUri = if (it.isBlank()) null else Uri.parse(it)
                    },
                    label = { Text("O ingresa URL") },
                    placeholder = { Text("https://example.com/plant.jpg") },
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
                                alreadyWatered = alreadyWateredStatus,
                                imageUri = imageUri.ifBlank { null }
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

fun createTempImageUri(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir("Pictures")
    val file = File.createTempFile("PLANT_${timeStamp}_", ".jpg", storageDir)
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
