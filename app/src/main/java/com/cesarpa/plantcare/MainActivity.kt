package com.cesarpa.plantcare

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cesarpa.plantcare.ui.screens.CreatePlantDialog
import com.cesarpa.plantcare.ui.screens.ProfileScreen
import com.cesarpa.plantcare.ui.theme.PlantCareTheme
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModel
import com.cesarpa.plantcare.ui.viewmodel.PlantViewModelFactory
import com.cesarpa.plantcare.ui.viewmodel.ProfileViewModel
import com.cesarpa.plantcare.ui.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val app = application as PlantCareApplication
            val plantViewModel: PlantViewModel = viewModel(
                factory = PlantViewModelFactory(app.repository)
            )
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(app.userPreferencesRepository)
            )

            PlantCareTheme {
                PlantCareApp(plantViewModel, profileViewModel)
            }
        }
    }
}

@Composable
fun PlantCareApp(plantViewModel: PlantViewModel, profileViewModel: ProfileViewModel) {
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
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        val userName by profileViewModel.userName.collectAsState()
                        Column {
                            Greeting(name = userName)
                            PlantList(plantViewModel)
                        }
                    }
                    AppDestinations.FAVORITES -> {
                        // For now reuse PlantList or implement dedicated favorites
                        PlantList(plantViewModel)
                    }
                    AppDestinations.PROFILE -> {
                        ProfileScreen(profileViewModel)
                    }
                }
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
    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

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
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "La hora actual es: $currentTime",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
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
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (plant.imageUri != null) {
                            AsyncImage(
                                model = plant.imageUri,
                                contentDescription = plant.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
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

                            val tickerFlow = remember(plant.id) {
                                flow {
                                    while (true) {
                                        emit(System.currentTimeMillis())
                                        delay(1000)
                                    }
                                }
                            }
                            val currentTime by tickerFlow.collectAsState(initial = System.currentTimeMillis())

                            val nextWateringTime = plant.lastWatered + (plant.waterInterval * 60000L)
                            val remainingMillis = max(0L, nextWateringTime - currentTime)
                            val isDue = remainingMillis == 0L

                            if (!isDue) {
                                val rDays = remainingMillis / (24 * 3600 * 1000)
                                val rHours = (remainingMillis % (24 * 3600 * 1000)) / (3600 * 1000)
                                val rMinutes = (remainingMillis % (3600 * 1000)) / (60 * 1000)
                                val rSeconds = (remainingMillis % (60 * 1000)) / 1000

                                val countdownText = buildString {
                                    append("Próximo riego en: ")
                                    if (rDays > 0) append("${rDays}d ")
                                    if (rHours > 0) append("${rHours}h ")
                                    if (rMinutes > 0) append("${rMinutes}m ")
                                    append("${rSeconds}s")
                                }
                                Text(
                                    text = countdownText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Text(
                                text = if (!isDue) "Estado: Ya regada ✅" else "Estado: Necesita agua 🚿",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (!isDue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = { viewModel.waterPlant(plant) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Regar Planta 🚿")
                            }
                        }
                    }
                }
            }
        }
    }
}
