package com.app.expresstaxiconductor.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {

    public static Location loc = null;
    Location myLocation;
    private LocationManager locationManager = null;

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            myLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            loc = location;
            System.out.println("Latitud " + location.getLatitude());
            System.out.println("Longitud " + location.getLongitude());

            myLocation.set(location);
        }
    }

    LocationListener[] mLocationListener = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocationManager();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2000, 0, mLocationListener[1]
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 2000, 0, mLocationListener[0]
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            for (LocationListener locationListener : mLocationListener) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    private void initLocationManager(){
        if(locationManager == null){
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}