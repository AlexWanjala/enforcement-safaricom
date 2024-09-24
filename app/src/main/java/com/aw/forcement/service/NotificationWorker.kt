package com.aw.forcement.service

import Json4Kotlin_Base
import android.annotation.SuppressLint
import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aw.forcement.R
import com.aw.forcement.history.ParkingHistory
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Call your function here
        callFunction()

        // Show notification


        // Return success
        return Result.success()
    }

    private fun callFunction() {

        val formData = listOf(
            "function" to "getNotifications",
        )

        executeRequest(formData, parking, object : CallBack {
            @SuppressLint("SuspiciousIndentation")
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if (response.success) {
                    // Use applicationContext instead of this@NotificationWorker
                    val numberPlate = getValue(applicationContext, "plateNumber")
                    if (numberPlate != response.data.queries[0].whichitem) {
                        sendNotification(response.data.queries[0].whichitem, response.data.queries[0].addressString)
                        save(applicationContext, "plateNumber", response.data.queries[0].whichitem)
                    }
                }
            }

            override fun onFailure(result: String?) {
                // Handle failure
            }
        })
    }

    private fun sendNotification(number: String, address: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channel_id"
        val channelName = "Channel Name"

        // Create an intent to open the desired activity
        val intent = Intent(applicationContext, ParkingHistory::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pass any data to the activity if needed
            putExtra("number", number)
            putExtra("address", address)
        }

        // Create a PendingIntent to launch the activity
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,  // Request code, if needed
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // For Android 12+
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Reminder")
            .setContentText("$number has not paid for parking in $address")
            .setSmallIcon(R.drawable.bell)  // Replace with your app icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  // Attach the PendingIntent to the notification
            .setAutoCancel(true)  // Dismiss the notification when clicked
            .build()

        notificationManager.notify(1, notification)
    }
}
