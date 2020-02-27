package io.moonshard.moonshard.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.moonshard.moonshard.MainApplication
import java.io.IOException
import java.util.*

class LocationService : Service() {

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            MainApplication.setCurrentLocation(location)
            getAddress(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            val test = ""
        }

        override fun onProviderEnabled(provider: String) {
            val test = ""

        }

        override fun onProviderDisabled(provider: String) {
            val test = ""
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun onServiceStart() {
        createLocationListener()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onServiceStart()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun createLocationListener() {
        try {
            val mLocationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, (1000 * 10).toLong(), 10f, locationListener
            )
            mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, (1000 * 10).toLong(), 10f, locationListener
            )
        } catch (e: Exception) {
            Log.d("error", "Permissions GSP off")
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("привет")
                .setContentText("как дела").build()

            startForeground(1, notification)
        }
    }


    internal fun getAddress(location: Location) {
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName // Only if available else return NULL
            MainApplication.setAdress(address)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}