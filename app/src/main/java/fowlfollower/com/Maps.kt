package fowlfollower.com

import androidx.appcompat.widget.Toolbar
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

class Maps : AppCompatActivity(), OnMapReadyCallback  {

    private lateinit var toolbar_add_place: Toolbar
    private var myMap: GoogleMap? = null
    private val FINE_PERMISSION_CODE = 1
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val greenMarkers: MutableList<Marker> = mutableListOf()
    private lateinit var btnSaveLocation: Button
    private lateinit var btnNavigateTop: Button
    private var observationsList: List<Observations.Observation>? = null // Declare observationsList as a property


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        toolbar_add_place = findViewById(R.id.toolbar_add_place)

        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        btnSaveLocation = findViewById(R.id.btnSaveLocation)
        btnSaveLocation.setOnClickListener {
            saveLocationAndOpenCreateObservation()
        }

        observationsList =
            intent.getParcelableArrayListExtra<Observations.Observation>("observationsList")?.toList()

        // Pass observationsList to onMapReady
        observationsList?.let {
            onMapReadyCallback(it)
        }

        // Initialize the top navigation button
        btnNavigateTop = findViewById(R.id.btnNavigateTop)
        btnNavigateTop.visibility = View.INVISIBLE
        btnNavigateTop.setOnClickListener {
            val selectedMarker = greenMarkers.find { it.isInfoWindowShown }
            selectedMarker?.let {
                val markerPosition = it.position
                Toast.makeText(
                    this@Maps,
                    "Navigate to: ${markerPosition.latitude}, ${markerPosition.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

                val origin = "${currentLocation!!.latitude},${currentLocation!!.longitude}"
                val destination = "${markerPosition.latitude},${markerPosition.longitude}"

                getDirections(origin, destination)
            }

            // Hide the top navigation button
            btnNavigateTop.visibility = View.INVISIBLE

            // Show the Save Observation button
            btnSaveLocation.visibility = View.VISIBLE
        }

        val btnShowObservations = findViewById<Button>(R.id.btnShowObservations)
        btnShowObservations.setOnClickListener {
            fetchObservationsFromFirebase { observationsList ->
                // Pass the observationsList to Maps activity
                val intent = Intent(this, Maps::class.java)
                intent.putParcelableArrayListExtra("observationsList", ArrayList(observationsList))
                startActivity(intent)
            }
        }
    }

    private fun fetchObservationsFromFirebase(callback: (List<Observations.Observation>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val observationsRef = database.getReference("Bird Observations")

        observationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newObservationsList = mutableListOf<Observations.Observation>()

                for (observationSnapshot in dataSnapshot.children) {
                    val title = observationSnapshot.child("title").getValue(String::class.java)
                    val description =
                        observationSnapshot.child("description").getValue(String::class.java)
                    val date = observationSnapshot.child("date").getValue(String::class.java)
                    val location =
                        observationSnapshot.child("location").getValue(String::class.java)
                    val imageUrl =
                        observationSnapshot.child("imageUrl").getValue(String::class.java)
                    val latitude =
                        observationSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude =
                        observationSnapshot.child("longitude").getValue(Double::class.java)

                    if (title != null && description != null && date != null && location != null &&
                        imageUrl != null && latitude != null && longitude != null
                    ) {
                        val observation = Observations.Observation(
                            title,
                            description,
                            date,
                            location,
                            imageUrl,
                            latitude,
                            longitude
                        )
                        newObservationsList.add(observation)
                    }
                }

                // Trigger the callback with the updated list
                callback(newObservationsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                // You may want to log the error or show a message to the user
            }
        })
    }

    private fun saveLocationAndOpenCreateObservation() {
        if (currentLocation != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Save a new observation?")
            builder.setMessage("Do you want to save a new observation at your current location?")
            builder.setPositiveButton("Yes") { _, _ ->
                saveLocation(currentLocation!!)
                val intent = Intent(this, CreateObservations::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("No") { _, _ ->
            }

            val dialog = builder.create()
            dialog.show()
        } else {
            Toast.makeText(
                this,
                "Current location not available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveLocation(location: Location) {
        val sharedPreferences = getSharedPreferences("LocationData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", location.latitude.toFloat())
        editor.putFloat("longitude", location.longitude.toFloat())
        editor.apply()
    }

    private fun getLastLocation() {
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }

        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(OnSuccessListener<Location> { location ->
            if (location != null) {
                currentLocation = location
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@Maps)
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied, please allow",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val initialLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        myMap?.addMarker(MarkerOptions().position(initialLocation).title("Your Current Location"))

        observationsList?.let {
            onMapReadyCallback(it)
        }

        myMap?.setOnMarkerClickListener { marker ->
            if (marker in greenMarkers) {
                marker.showInfoWindow()
                // Show the Navigate button and hide the Save Observation button when a green marker is clicked
                btnNavigateTop.visibility = View.VISIBLE
                btnSaveLocation.visibility = View.INVISIBLE
            } else {
                // Hide the Navigate button and show the Save Observation button when another marker is clicked
                btnNavigateTop.visibility = View.INVISIBLE
                btnSaveLocation.visibility = View.VISIBLE
            }
            false
        }

        myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))
    }


    private fun updateButtonVisibility(selectedMarker: Marker?) {
        if (selectedMarker != null && selectedMarker in greenMarkers) {
            // If a green marker is selected, show the Navigate button and hide the Save Observation button
            btnNavigateTop.visibility = View.VISIBLE
            btnSaveLocation.visibility = View.INVISIBLE
        } else {
            // If no green marker is selected, hide the Navigate button and show the Save Observation button
            btnNavigateTop.visibility = View.INVISIBLE
            btnSaveLocation.visibility = View.VISIBLE
        }
    }

    private fun getDirections(origin: String, destination: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Navigate to hotspot?")
        alertDialogBuilder.setMessage("\nNavigating from\n\n Current location:\n $origin\n\n to \n\n Bird hotspot:\n $destination")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Call a function to get directions and display the route
            val apiKey = "AIzaSyCnOdHqkKH_0nE5p33jpSdFlX5nn-Ix6PQ"
            val context = GeoApiContext.Builder().apiKey(apiKey).build()
            val directionsResult: DirectionsResult = DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .origin(origin)
                .destination(destination)
                .await()
            displayRoute(directionsResult)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            // Handle the case where the user declined navigation
        }
        alertDialogBuilder.show()
    }

    private fun displayRoute(directionsResult: DirectionsResult) {
        myMap?.clear()
        val route = directionsResult.routes[0]
        val polylineOptions = PolylineOptions()
        for (leg in route.legs) {
            for (step in leg.steps) {
                val path = step.polyline.decodePath()
                for (point in path) {
                    polylineOptions.add(LatLng(point.lat, point.lng))
                }
            }
        }

        myMap?.addPolyline(polylineOptions)

        val startMarker = MarkerOptions()
            .position(LatLng(route.legs[0].startLocation.lat, route.legs[0].startLocation.lng))
            .title("Start")
        val endMarker = MarkerOptions()
            .position(LatLng(route.legs.last().endLocation.lat, route.legs.last().endLocation.lng))
            .title("End")

        myMap?.addMarker(startMarker)
        myMap?.addMarker(endMarker)

        val boundsBuilder = LatLngBounds.builder()
        for (leg in route.legs) {
            boundsBuilder.include(LatLng(leg.startLocation.lat, leg.startLocation.lng))
            boundsBuilder.include(LatLng(leg.endLocation.lat, leg.endLocation.lng))
        }

        val bounds = boundsBuilder.build()
        val padding = 100
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        myMap?.moveCamera(cameraUpdate)
    }

    private fun onMapReadyCallback(observationsList: List<Observations.Observation>) {


        if (observationsList != null) {
            for (observation in observationsList) {
                val observationLocation = LatLng(observation.latitude, observation.longitude)
                val orangeMarker = myMap?.addMarker(
                    MarkerOptions()
                        .position(observationLocation)
                        .title(observation.title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                )
                orangeMarker?.let { greenMarkers.add(it) }
            }
        } else {
            // Handle the case where observationsList is null
            Toast.makeText(this, "Observations list is null", Toast.LENGTH_SHORT).show()
        }
    }
}

