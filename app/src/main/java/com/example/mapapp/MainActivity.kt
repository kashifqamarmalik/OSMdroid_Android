package com.example.mapapp


import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity()  {

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)


        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(16.0)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10)
        }


            fusedLocationClient.lastLocation.addOnCompleteListener(this) {task ->
                if (task.isSuccessful && task.result != null) {
                    Log.d("GEOLOCATION", "latitude: ${task.result!!.latitude} and longitude: ${task.result!!.longitude}")
                    //location_txt.setText("latitude: ${task.result!!.latitude} and longitude: ${task.result!!.longitude}")
                    map.controller.setCenter(GeoPoint(task.result!!.latitude, task.result!!.longitude))
                    val geocoder = Geocoder(this)
                    val list = geocoder.getFromLocation(task.result!!.latitude, task.result!!.longitude, 1)
                    val address = list[0].getAddressLine(0)
                    location_txt.setText("${address}")

                }
            }


        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), map)
        map.overlays.add(myLocationOverlay)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()

        val myScaleBarOverlay = ScaleBarOverlay(map)
        map.getOverlays().add(myScaleBarOverlay)

        }
    }
