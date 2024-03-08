package fowlfollower.com

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fowlfollower.com.adapters.ObservationsAdapter


//import fowlfollower.com.adapters.ObservationsAdapter
//import fowlfollower.com.models.ObservationModel

class Observations : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerlayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var add: FloatingActionButton
    lateinit var toolbar_add_place: Toolbar
    //lateinit var images: ImageView

    private lateinit var observationsAdapter: ObservationsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var observationsList: MutableList<Observation>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observations)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        observationsList = mutableListOf()
        observationsAdapter = ObservationsAdapter(observationsList)
        recyclerView.adapter = observationsAdapter
        // Fetch observations from Firebase
        fetchObservationsFromFirebase{ observationsList ->
            // Update the UI with observations data
            observationsList.let {
                this.observationsList.clear()
                this.observationsList.addAll(it)
                observationsAdapter.notifyDataSetChanged()
            }

            val intent = Intent(this, Maps::class.java)
            intent.putParcelableArrayListExtra("observationsList", ArrayList(observationsList))
            //startActivity(intent)
        }
        toolbar_add_place = findViewById(R.id.toolbar_add_place)


        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }


        add = findViewById(R.id.add)
        add.setOnClickListener {

            val intent = Intent(this, CreateObservations::class.java)
            startActivity(intent)


            drawerlayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.nav_view)
            //toolbar = findViewById(R.id.toolbar1)


//-----------------------------------Menu-----------------------------------------------------------
            /*setSupportActionBar(toolbar)
            navigationView.bringToFront()
            val toggle = ActionBarDrawerToggle(
                this,
                drawerlayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerlayout.addDrawerListener(toggle)
            toggle.syncState()
            navigationView.setNavigationItemSelectedListener(this)
            navigationView.setCheckedItem(R.id.nav_observations)*/
        }

         fun onBackPressed() {
             if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                 drawerlayout.closeDrawer(GravityCompat.START)
             } else {
                 super.onBackPressed()
             }
         }

    }

    private fun fetchObservationsFromFirebase(callback: (List<Observations.Observation>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val observationsRef = database.getReference("Bird Observations")

        observationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newObservationsList = mutableListOf<Observation>()

                for (observationSnapshot in dataSnapshot.children) {
                    val title = observationSnapshot.child("title").getValue(String::class.java)
                    val description = observationSnapshot.child("description").getValue(String::class.java)
                    val date = observationSnapshot.child("date").getValue(String::class.java)
                    val location = observationSnapshot.child("location").getValue(String::class.java)
                    val imageUrl = observationSnapshot.child("imageUrl").getValue(String::class.java)

                    val latitude = observationSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = observationSnapshot.child("longitude").getValue(Double::class.java)

                    if (title != null && description != null && date != null && location != null &&
                        imageUrl != null && latitude != null && longitude != null
                    ) {
                        val observation = Observation(title, description, date, location, imageUrl , latitude, longitude)
                        newObservationsList.add(observation)
                    }
                }


                // Update the observations list and notify the adapter
                observationsList.clear()
                observationsList.addAll(newObservationsList)
                observationsAdapter.notifyDataSetChanged()

                newObservationsList.apply {
                    observationsList.clear()
                    observationsList.addAll(this)
                }

                // Trigger the callback with the updated list
                callback(newObservationsList)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                Log.e("Observations", "Error fetching observations from Firebase", databaseError.toException())
            }
        })
    }

    data class Observation(
        val title: String = "",
        val description: String = "",
        val date: String = "",
        val location: String = "",
        val imageUrl: String = "",
        //val audioFileUri: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    ) : Parcelable {

        // This constructor is needed for the Parcelable implementation
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            //parcel.readString()?:"",
            parcel.readDouble(),
            parcel.readDouble()
        )

        // Implement the Parcelable interface method: writeToParcel
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeString(date)
            parcel.writeString(location)
            parcel.writeString(imageUrl)
            //parcel.writeString(audioFileUri)
            parcel.writeDouble(latitude)
            parcel.writeDouble(longitude)
        }

        // Implement the Parcelable interface method: describeContents
        override fun describeContents(): Int {
            return 0
        }

        // Companion object is required for the Parcelable interface
        companion object CREATOR : Parcelable.Creator<Observation> {
            // Implement the Parcelable interface method: createFromParcel
            override fun createFromParcel(parcel: Parcel): Observation {
                return Observation(parcel)
            }

            // Implement the Parcelable interface method: newArray
            override fun newArray(size: Int): Array<Observation?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }

            R.id.nav_map -> {
                val intent = Intent(this, Maps::class.java)
                startActivity(intent)
            }

            R.id.nav_observations -> {
                val intent = Intent(this, Observations::class.java)
                startActivity(intent)
            }

            R.id.nav_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }

            R.id.nav_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }
}
