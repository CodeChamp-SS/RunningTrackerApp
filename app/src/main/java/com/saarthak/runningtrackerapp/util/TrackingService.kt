package com.saarthak.runningtrackerapp.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.util.Constants.ACTION_PAUSE_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.ACTION_START_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.ACTION_STOP_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.FASTEST_UPDATE_INTERVAL
import com.saarthak.runningtrackerapp.util.Constants.LOCN_UPDATE_INTERVAL
import com.saarthak.runningtrackerapp.util.Constants.NOTIFICATION_CHANNEL_ID
import com.saarthak.runningtrackerapp.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.saarthak.runningtrackerapp.util.Constants.NOTIFICATION_ID
import com.saarthak.runningtrackerapp.util.Constants.TIMER_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Coord = MutableList<LatLng>
typealias Coords = MutableList<Coord>

@AndroidEntryPoint
class TrackingService: LifecycleService() {

    var firstRun = true

    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var builder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private val timeSec = MutableLiveData<Long>()
    private var timerEnabled = false
    private var lapTime = 0L
    private var totTime = 0L
    private var timeStarted = 0L
    private var lastSecTimeStamp = 0L

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathCoord = MutableLiveData<Coords>()
        val timeMs = MutableLiveData<Long>()
    }

    private fun startTimer(){
        addEmptyCoord()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        timerEnabled = true

        CoroutineScope(Main).launch {
            while(isTracking.value!!){
                lapTime = System.currentTimeMillis() - timeStarted
                timeMs.postValue(lapTime + totTime)

                if(timeMs.value!! >= lastSecTimeStamp + 1000){
                    timeSec.postValue(timeSec.value!! + 1)
                    lastSecTimeStamp += 1000
                }

                delay(TIMER_DELAY)
            }

            totTime += lapTime
        }
    }

    private fun initValues(){
        isTracking.postValue(false)
        pathCoord.postValue(mutableListOf())
        timeSec.postValue(0)
        timeMs.postValue(0)
    }

    private fun pauseService(){
        isTracking.postValue(false)
        timerEnabled = false
    }

    private fun addEmptyCoord() = pathCoord.value?.apply {
        add(mutableListOf())
        pathCoord.postValue(this)
    } ?: pathCoord.postValue(mutableListOf(mutableListOf()))

    private fun addCoord(locn: Location?){
        locn?.let {
            val posn = LatLng(it.latitude, it.longitude)
            pathCoord.value?.apply {
                last().add(posn)
                pathCoord.postValue(this)
            }
        }
    }

    val locnCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            if(isTracking.value!!){
                p0?.locations?.let {
                    for(locn in it){
                        addCoord(locn)

                        Timber.d("Coord: ${locn.latitude}, ${locn.longitude}")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocnTracking(tracking: Boolean){
        if(tracking){
            if(Utilities.locnPermissionsCheck(this)){
                val locnReq = LocationRequest().apply {
                    interval = LOCN_UPDATE_INTERVAL
                    fastestInterval = FASTEST_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    locnReq,
                    locnCallback,
                    Looper.getMainLooper()
                )
            }
        }
        else fusedLocationProviderClient.removeLocationUpdates(locnCallback)
    }

    private fun updateNotificationState(tracking: Boolean){
        val notificationText = if(tracking) "Pause" else "Resume"

        val pendingIntent = if(tracking){
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }

            getService(this, 3, pauseIntent, FLAG_UPDATE_CURRENT)
        } else{
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_SERVICE
            }

            getService(this, 4, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // todo: clear all the previous actions attatched to the notification before we update the notification
        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if(! serviceKilled){
            curNotificationBuilder = builder
                .addAction(R.drawable.ic_pause, notificationText, pendingIntent)

            manager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager){
        // if we choose the importance greater than low then whenever it's updated the phone will ring => unnecessary
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)

        manager.createNotificationChannel(channel)
    }

    private fun startForegroundService(){
        isTracking.postValue(true)
        startTimer()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
             createNotificationChannel(manager)
         }

        startForeground(NOTIFICATION_ID, builder.build())

        timeSec.observe(this, Observer {
            if(! serviceKilled){
                val notification = curNotificationBuilder
                    .setContentText(Utilities.getFormattedTime(it * 1000))

                manager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    private fun killService(){
        serviceKilled = true
        firstRun = true
        pauseService()
        initValues()

        stopForeground(true)
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = builder

        initValues()

        isTracking.observe(this, Observer {
            updateLocnTracking(it)
            updateNotificationState(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_SERVICE -> {
                    if(firstRun){
                        startForegroundService()
                        firstRun = false
                        Timber.d("started service")
                    }
                    else {
                        startTimer()
                        Timber.d("resumed service")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("stopped service")
                    killService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}