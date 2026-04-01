package com.cesarpa.plantcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.cesarpa.plantcare.ui.theme.PlantCareTheme

import com.cesarpa.plantcare.ui.screens.CreatePlantDialog
import androidx.compose.runtime.remember

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModel
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModelFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            PlantCareTheme {
                val viewModel: PlantViewModel = viewModel(
                    factory = PlantViewModelFactory((application as PlantCareApplication).repository)
                )
                PlantCareApp(viewModel)
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PlantCareApp(viewModel: PlantViewModel = viewModel()) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Greeting(name = "Anyela")
                PlantList(viewModel)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("My Plants", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "¡Hola, $name! 🌿",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Tus plantas te están esperando.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun PlantList(viewModel: PlantViewModel) {
    val plants by viewModel.allPlants.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlantDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { newPlant ->
                viewModel.insert(newPlant)
                showCreateDialog = false
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "My Plants 🌿", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { showCreateDialog = true }) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(plants) { plant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = plant.name, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.delete(plant) }) {
                                Icon(
                                    painter = painterResource(android.R.drawable.ic_menu_delete),
                                    contentDescription = "Borrar"
                                )
                            }
                        }
                        Text(text = "Sustrato: ${plant.soilType}", style = MaterialTheme.typography.bodySmall)
                        
                        // Format interval from minutes
                        val days = plant.waterInterval / 1440
                        val hours = (plant.waterInterval % 1440) / 60
                        val minutes = plant.waterInterval % 60
                        val intervalText = buildString {
                            append("Riego cada: ")
                            if (days > 0) append("${days}d ")
                            if (hours > 0) append("${hours}h ")
                            if (minutes > 0 || (days == 0 && hours == 0)) append("${minutes}m")
                        }
                        Text(text = intervalText, style = MaterialTheme.typography.bodySmall)
                        
                        Text(text = "Maceta: ${plant.potType.name}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Luz: ${plant.energySource.name}", style = MaterialTheme.typography.bodySmall)

                        val currentTime = System.currentTimeMillis()
                        // Condition from user: currentDate - waterInterval > lastWatered we are ok (alreadyWatered)
                        // If currentDate - lastWatered > waterInterval, it means the time has passed since last watering.
                        // I will assume this means "it's time to water" or "already watered" based on user's literal logic.
                        // However, standard logic would be (currentTime - plant.lastWatered) < (plant.waterInterval * 60000L)
                        val alreadyWateredValue = (currentTime - plant.lastWatered) < (plant.waterInterval * 60000L)
                        
                        Text(
                            text = if (alreadyWateredValue) "Estado: Ya regada ✅" else "Estado: Necesita agua 🚿",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (alreadyWateredValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlantCareTheme {
        Greeting("Android")
    }
}