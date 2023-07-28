package com.seton_develops.natpak

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import com.seton_develops.natpak.databinding.ActivityMainBinding


private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val apiKey: String = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)

        binding.buttonTest.setOnClickListener {
            requestLocation()
            binding.textviewTest.text = "james's text"
            Log.i("TEST", apiKey)
        }

    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            requestLocation()
        }
        else {
            getUserLocationPermissions()

        }

    }

    private fun getUserLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )

        }

    }

    @SuppressWarnings("MissingPermission") //Gets around permission check
    private fun requestLocation() {
        var locationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })

        locationTask.addOnSuccessListener {
            if (it != null) {
                Toast.makeText(this,
                    "latitude: ${it.latitude} longitude: ${it.longitude}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else { //Todo: remove this. for checking purposes only
                Toast.makeText(this,
                    "error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        locationTask.addOnFailureListener {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //Permission granted
        if (requestCode == REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

            requestLocation()
        }
        //Permission has not been granted
        else {
            Toast.makeText(this,
                "Location Tracking not granted. Enable permissions to enable tracking",
                Toast.LENGTH_LONG).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }




}