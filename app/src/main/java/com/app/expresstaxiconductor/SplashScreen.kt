package com.app.expresstaxiconductor

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.DialogInterface
import android.provider.Settings
import com.app.expresstaxiconductor.utils.LocationService


class SplashScreen : AppCompatActivity() {
    val REQUEST_LOCATION=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        checkPermissions()

    }



    fun checkPermissions(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermission()
        }else{
            launchSplashScreen()
            Toast.makeText(this,"Permisos aceptados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_LOCATION ->{
                if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    launchSplashScreen()
                    Toast.makeText(this,"Permisos aceptados", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Permisos no aceptados", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    fun launchSplashScreen(){
        Handler().postDelayed(Runnable {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },3000)
    }
}