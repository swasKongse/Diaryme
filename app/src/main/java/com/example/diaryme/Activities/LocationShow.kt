package com.example.diaryme.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.diaryme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates


class LocationShow : AppCompatActivity(), OnMapReadyCallback {
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_location)

        longitude = intent.getDoubleExtra("longitude",0.0)
        latitude = intent.getDoubleExtra("latitude",0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val loc = LatLng(latitude, longitude)
        googleMap.addMarker(
            MarkerOptions()
                .position(loc)
                .title("Your Friend's Location")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
    }
}