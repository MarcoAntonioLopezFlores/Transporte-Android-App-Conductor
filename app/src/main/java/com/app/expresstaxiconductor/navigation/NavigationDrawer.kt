package com.app.expresstaxiconductor.navigation

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.expresstaxiconductor.LoginActivity
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.databinding.ActivityNavigationDrawerBinding
import com.app.expresstaxiconductor.utils.locationback.LocationUpdatesService
import com.app.expresstaxiconductor.utils.locationback.StatusLocation

class NavigationDrawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationDrawerBinding
    private var myReceiver: MyReceiver? = null
    private var flag:Boolean = false
    private var mService: LocationUpdatesService? = null

    private var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mapsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
        myReceiver = MyReceiver()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut(){
        val intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_signOut->{
                signOut()
            }
        }
        return true
    }
    private val service: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder =
                service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        s: String
    ) {

        if (s == StatusLocation.KEY_REQUESTING_LOCATION_UPDATES) {
            if(!flag) {
                setButtonsState(
                    sharedPreferences.getBoolean(
                        StatusLocation.KEY_REQUESTING_LOCATION_UPDATES,
                        false
                    )
                )
            }

        }
    }

    private fun setButtonsState(requestingLocationUpdates: Boolean) {
        if (!requestingLocationUpdates) {
            val alertDialog =
                AlertDialog.Builder(this).create()
            alertDialog.setCancelable(false)
            alertDialog.setMessage("Express Taxi esta accediendo a su ubicación")
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ ->
                dialog.dismiss()
                /*val intent = Intent(this, NavigationDrawer::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)*/
                //finish()


                mService!!.requestLocationUpdates()
            }
            alertDialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)


// Restore the state of the buttons when the activity (re)launches.
        //System.out.println("onStart" +mService.toString());
        //System.out.println("onStart -> "+ StatusLocation.requestingLocationUpdates(this));


        if(mService != null){
            setButtonsState(StatusLocation.requestingLocationUpdates(this))
        }else{
            setButtonsState(false)
        }

        bindService(
            Intent(this, LocationUpdatesService::class.java), service,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            myReceiver!!,
            IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
        )
    }
    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
        super.onPause()
    }

    override fun onStop() {
        if (mBound) {
            unbindService(service)
            mBound = false
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

}