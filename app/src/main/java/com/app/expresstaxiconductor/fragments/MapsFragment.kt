package com.app.expresstaxiconductor.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.expresstaxiconductor.LoginActivity
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.models.*
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.app.expresstaxiconductor.utils.LocationService
import com.app.expresstaxiconductor.utils.api.APIFirebase
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_maps.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener{
    private lateinit var mMap: GoogleMap
    private val FILTRO_CHAT = "broadcast_servicio"
    private val FILTRO_DEFAULT = "broadcast_default"
    private val KEY = "AAAADYblWbE:APA91bF3zj6eBR1Hbl75OTVMd_k7dnR4znuw2BiNxY0iKrKRrP0ZNxnlDevqSbWeAdYmoyU-KJ8F3CKuFEB6CeDykvzDNe_P_JByhLl792zh40pcZXYzL--uPoJrSOI8MtdpUKcECVK2"
    private val avance = if(PrefsApplication.prefs.getData("avance").isNotEmpty()) PrefsApplication.prefs.getData("avance") else ""

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
            //loadNavigationView("18.8389033","-99.2395182")
            //findNavController().navigate(R.id.detailsFragment)
            asignarConductor()
        }

        if(PrefsApplication.prefs.getData("correo").isEmpty()){
            startActivity(Intent(context, LoginActivity::class.java))
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcast, IntentFilter(FILTRO_CHAT))
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastDefault, IntentFilter(FILTRO_DEFAULT))

        return viewRoot
    }

    val broadcast = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
//            val mapsFragment: Fragment = MapsFragment()
//            val fragmentTransaction = fragmentManager?.beginTransaction()
////                fragmentTransaction?.replace(R.id.nav_host_fragment_content_navigation_drawer, mapsFragment)
//            fragmentTransaction?.detach(mapsFragment)
//            fragmentTransaction?.attach(mapsFragment)
//            fragmentTransaction?.commit()
//            val mapsFragment: Fragment = MapsFragment()
//            val fragmentTransaction = fragmentManager?.beginTransaction()
//            fragmentTransaction?.replace(R.id.nav_host_fragment_content_navigation_drawer, mapsFragment)
//            fragmentTransaction?.commit()
        }
    }

    val broadcastDefault = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null){
                PrefsApplication.prefs.save("avance", intent.getStringExtra("avance").toString())
                if(intent.getStringExtra("avance") == "Cancelar"){
                    PrefsApplication.prefs.delete("servicio_id")
                }
//                val mapsFragment: Fragment = MapsFragment()
//                val fragmentTransaction = fragmentManager?.beginTransaction()
////                fragmentTransaction?.replace(R.id.nav_host_fragment_content_navigation_drawer, mapsFragment)
//                fragmentTransaction?.detach(mapsFragment)
//                fragmentTransaction?.attach(mapsFragment)
//                fragmentTransaction?.commit()
            }
        }

    }

    fun consultar() {
        val apiService: APIService = RetrofitClient.getAPIService()
        println(PrefsApplication.prefs.SEVICIO_ID)
        val id = PrefsApplication.prefs.getData("servicio_id").toLong()
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.obtenerServicio(TOKEN, id).enqueue(object: Callback<Servicio>{
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if(response.isSuccessful){
                    val servicio = response.body() as Servicio
                    findLocation(servicio.latitudInicial, servicio.longitudInicial)
                }
            }

            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                Toast.makeText(context, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun asignarConductor(){
        val apiService: APIService = RetrofitClient.getAPIService()

        val conductor = Conductor(0, null, null, Usuario(PrefsApplication.prefs.getData("user_id").toLong(), "", "", "","","","","",true, null, null), null)
        val servicio = Servicio(PrefsApplication.prefs.getData("servicio_id").toLong(), null, 0f, 0.0,0.0,0.0,0.0,null, null, conductor)
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.asignarConductor(TOKEN, servicio).enqueue(object: Callback<Servicio>{
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if(response.isSuccessful){
                    val servicio = response.body() as Servicio
                    //cambiarEstado(servicio)
                    PrefsApplication.prefs.save("tokenclientfb", servicio.cliente!!.usuario.tokenfb!!)
                    enviarNotificacion(servicio)
                    PrefsApplication.prefs.save("servicio_id", servicio.id.toString())
                    PrefsApplication.prefs.save("is_service", "true")
                    startActivity(Intent(context,DetailsDriverFragment::class.java))
                }
            }

            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                Toast.makeText(context, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun enviarNotificacion(servicio: Servicio){
        val apiFirebase: APIFirebase = RetrofitClient.getAPIFirebase()
        val notificacion = Notificacion(PrefsApplication.prefs.getData("tokenclientfb"), Datos(servicio.id!!.toString(), "Confirmacion","Servicio aceptado","Ingrese para ver los detalles"))

        apiFirebase.enviarNotificacion("key=$KEY", notificacion).enqueue(object: Callback<JSONObject>{
            override fun onResponse(
                call: Call<JSONObject>,
                response: Response<JSONObject>
            ) {
                if(response.isSuccessful){
                    println("Se envió la notificación")
                }
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Toast.makeText(context, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun cambiarEstado(servicio: Servicio){
        val apiService: APIService = RetrofitClient.getAPIService()
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"
        servicio.estado = Estado(null, "Aceptado")

        apiService.cambiarEstado(TOKEN, servicio).enqueue(object: Callback<Servicio>{
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if(response.isSuccessful){

                }
            }
            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                println("No se cambió el estado")
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if(PrefsApplication.prefs.getData("servicio_id").isNotEmpty()){
            btnStartService.visibility = View.VISIBLE
            consultar()
        }else{
            btnStartService.visibility = View.GONE
        }
    }

    fun findLocation(latitud: Double, longitud: Double){
//        val geocoder = Geocoder(activity, Locale.getDefault())

//        val address = geocoder.getFromLocationName(direccion,5)

//        println("Lat: "+ address[0].latitude)
//        println("Long: "+ address[0].longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitud, longitud)))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitud, longitud),16.0f))
    }

    private fun findLatLong(latitud:Double, longitud:Double){
        val geocoder = Geocoder(activity, Locale.getDefault())

        val address = geocoder.getFromLocation(latitud,longitud, 5)


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
            val lat = mMap.cameraPosition.target.latitude
            val long = mMap.cameraPosition.target.longitude

            findLatLong(lat,long)
        }
    }

    private fun loadNavigationView(lat: String, lng: String) {
        val navigationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng&mode=d"))
        navigationIntent.setPackage("com.google.android.apps.maps")
        startActivity(navigationIntent)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }


}