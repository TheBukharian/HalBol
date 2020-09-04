package com.example.googlemapsexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.googlemapsexample.Models.EXTRA_LATLONG
import com.example.googlemapsexample.Models.Loc

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var location: Loc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        location=intent.getParcelableExtra(EXTRA_LATLONG)

        Toast.makeText(this,"${location.Longitude}",Toast.LENGTH_LONG).show()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lat=location.Latitude.toDouble()
        val long =location.Longitude.toDouble()


        val address = LatLng(lat,long)
        mMap.addMarker(MarkerOptions().position(address).title("Photo was taken there"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(address))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 16f))
    }
}
