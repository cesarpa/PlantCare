package com.cesarpa.plantcare.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.cesarpa.plantcare.PlantCareApplication
import com.cesarpa.plantcare.R
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class WateringWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val application = applicationContext as PlantCareApplication
        val repository = application.repository
        val plants = repository.allPlants.first()
        val currentTime = System.currentTimeMillis()

        plants.forEach { plant ->
            // Literal condition from user: currentDate - waterInterval > lastWatered we are ok
            // I will use it to decide if we notify or not.
            // If the time passed is more than the interval, it needs water.
            val intervalMillis = plant.waterInterval * 60000L
            val needsWater = (currentTime - plant.lastWatered) > intervalMillis
            
            if (needsWater) {
                sendNotification(plant.name)
            }
        }
        return Result.success()
    }

    private fun sendNotification(plantName: String) {
        val channelId = "watering_notifications"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Plant Watering", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Hora de regar! 🚿")
            .setContentText("Tu planta $plantName necesita agua.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(plantName.hashCode(), notification)
    }

    companion object {
        fun enqueue(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<WateringWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.NONE)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WateringWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
