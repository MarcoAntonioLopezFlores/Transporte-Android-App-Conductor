package com.app.expresstaxiconductor.utils.locationback

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.expresstaxiconductor.fragments.DetailsDriverFragment
import com.app.expresstaxiconductor.models.Conductor
import com.app.expresstaxiconductor.models.Localizacion
import com.app.expresstaxiconductor.models.Servicio
import com.app.expresstaxiconductor.models.Usuario
import com.app.expresstaxiconductor.navigation.NavigationDrawer
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LocationUpdatesService: Service() {

    private val mBinder: IBinder =
        LocalBinder()

    private var mChangingConfiguration = false
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null
    private var mLocation: Location? = null

    override fun onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if(loc!=null){
                    println("companion latitud: "+loc!!.latitude)
                    val apiService: APIService = RetrofitClient.getAPIService()

                    try{
                        val conductor = Conductor(0, null, null,
                            Usuario(PrefsApplication.prefs.getData("user_id").toLong(), "", "", "","","","","",true, null),
                            Localizacion(0, loc!!.longitude,loc!!.latitude)
                        )
                        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"
                        apiService.actualizarUbicacion(TOKEN, conductor).enqueue(object:
                            Callback<Conductor> {
                            override fun onResponse(call: Call<Conductor>, response: Response<Conductor>) {
                                if(response.isSuccessful){
                                    var conductor = response.body() as Conductor
                                    println("latitud conductor registrada: "+conductor.localizacion!!.latitud)
                                    println("Se actualizo la ubicacion")

                                }
                            }

                            override fun onFailure(call: Call<Conductor>, t: Throwable) {
                                println("ERROR al actualizar ubicacion")
                            }

                        })
                    }catch (e:Exception){

                    }

                }

                onNewLocation(locationResult.lastLocation)
            }
        }
        createLocationRequest()
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(com.app.expresstaxiconductor.R.string.app_name)
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val startedFromNotification = intent.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )

        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder {

        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {

        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {

        if (!mChangingConfiguration && StatusLocation.requestingLocationUpdates(this)) {
            startForeground(NOTIFICATION_ID, notification)
        }
        return true
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
    }


    fun requestLocationUpdates() {

        StatusLocation.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            StatusLocation.setRequestingLocationUpdates(this, false)

        }
    }


    private fun removeLocationUpdates() {

        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            StatusLocation.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            StatusLocation.setRequestingLocationUpdates(this, true)

        }
    }

    private val notification: Notification
        get() {
            val intent = Intent(this, LocationUpdatesService::class.java)

            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

            val servicePendingIntent = PendingIntent.getService(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


            val activityPendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(this, NavigationDrawer::class.java), 0
            )
            val builder =
                NotificationCompat.Builder(this)
                    .addAction(
                        com.app.expresstaxiconductor.R.mipmap.ic_launcher, "Abrir",
                        activityPendingIntent
                    )
                    .addAction(
                        com.app.expresstaxiconductor.R.mipmap.ic_launcher, "Detener ubicación",
                        servicePendingIntent
                    )
                    .setContentText("Express Taxi esta usando su ubicación")
                    .setContentTitle("Express Taxi")
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(com.app.expresstaxiconductor.R.drawable.ic_location)
                    .setTicker("Express Taxi esta usando su ubicación")
                    .setWhen(System.currentTimeMillis())


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
            }
            return builder.build()
        }


    private fun onNewLocation(location: Location) {
        mLocation = location
        loc = location

        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    inner class LocalBinder : Binder() {
        val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }



    companion object {
        var loc: Location? = null
        private const val PACKAGE_NAME =
            "com.google.android.gms.location.expresstaxiconductor.LocationUpdatesService"
        private val TAG = LocationUpdatesService::class.java.simpleName


        private const val CHANNEL_ID = "channel_01"
        const val ACTION_BROADCAST =
            "$PACKAGE_NAME.broadcast"
        const val EXTRA_LOCATION =
            "$PACKAGE_NAME.location"
        private const val EXTRA_STARTED_FROM_NOTIFICATION =
            PACKAGE_NAME +
                    ".started_from_notification"


        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 60000


        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2


        private const val NOTIFICATION_ID = 12345678
    }
}