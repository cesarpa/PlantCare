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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.cesarpa.plantcare.ui.theme.PlantCareTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.cesarpa.plantcare.data.model.EnergySource
import com.cesarpa.plantcare.data.model.Plant
import com.cesarpa.plantcare.data.model.PotType
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModel
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModelFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                Greeting(name = "PlantCare User")
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
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun PlantList(viewModel: PlantViewModel) {
    val plants by viewModel.allPlants.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "My Plants 🌿", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = {
                viewModel.insert(
                    Plant(
                        name = "Cactus de Cesar ${plants.size + 1}",
                        soilType = "Cactus mix",
                        potType = PotType.CLAY,
                        energySource = EnergySource.DIRECT_SUN,
                        waterInterval = 7,
                        lastWatered = System.currentTimeMillis()
                    )
                )
            }) {
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
                        Text(text = "Riego cada ${plant.waterInterval} días", style = MaterialTheme.typography.bodySmall)
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