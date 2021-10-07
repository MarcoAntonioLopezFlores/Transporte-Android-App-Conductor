package com.app.expresstaxiconductor.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.utils.LocationService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_maps.view.*
import java.util.*
import android.content.Intent
import android.net.Uri


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener{
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_maps, container, false)

        viewRoot.btnCenterLocalization.setOnClickListener {
            val punto = LatLng(
                LocationService.loc.latitude,
                LocationService.loc.longitude)

            mMap.moveCamera(CameraUpdateFactory.newLatLng(punto))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punto,16.0f))
        }

        viewRoot.btnStartService.setOnClickListener{
            loadNavigationView("18.8389033","-99.2395182")
        }

        viewRoot.btnSearchDirection.setOnClickListener{
            findLocation(edtAddressToSearch.text.toString())
        }
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    fun findLocation(direccion:String){
        val geocoder = Geocoder(activity, Locale.getDefault())

        val address = geocoder.getFromLocationName(direccion,5)

        println("Lat: "+address.get(0).latitude)
        println("Long: "+address.get(0).longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(address.get(0).latitude,address.get(0).longitude)))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(address.get(0).latitude,address.get(0).longitude),16.0f))
    }

    fun findLatLong(latitud:Double, longitud:Double){
        val geocoder = Geocoder(activity, Locale.getDefault())

        val address = geocoder.getFromLocation(latitud,longitud, 5)

        if(address.size>0){
            edtAddressToSearch.setText(address[0].getAddressLine(0))

        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.isMyLocationEnabled=true

        mMap.setOnCameraIdleListener {
            var lat = mMap.cameraPosition.target.latitude
            var long = mMap.cameraPosition.target.longitude

            findLatLong(lat,long)
        }
    }

    fun loadNavigationView(lat: String, lng: String) {
        val navigationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng&mode=d"))
        navigationIntent.setPackage("com.google.android.apps.maps")
        startActivity(navigationIntent)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }


}