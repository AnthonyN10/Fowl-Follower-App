package fowlfollower.com

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.Map

class Settings : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var drawerlayout: DrawerLayout
        lateinit var navigationView: NavigationView
        lateinit var toolbar: Toolbar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("settings")

        val btnsave = findViewById<Button>(R.id.btnSaveSet)
        btnsave.setOnClickListener {
            // Get selected values from UI components
            val maxDistance = findViewById<SeekBar>(R.id.seekBar2).progress
            val measuringSystem = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId).text.toString()
            val privacyOption = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.radioGroup1).checkedRadioButtonId).text.toString()
            val shareContactDetails = findViewById<RadioButton>(R.id.radioButton3).isChecked

            // Create a Settings object with the data
            val settings = SettingsData(maxDistance, measuringSystem, privacyOption, shareContactDetails)

            // Push the settings data to Firebase
            myRef.setValue(settings)

            Toast.makeText(this, "Settings have been saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        drawerlayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar1)


        setSupportActionBar(toolbar)
        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()


        navigationView.setNavigationItemSelectedListener (this)

        navigationView.setCheckedItem(R.id.nav_settings)

        @Override
        fun onBackPressed()
        {
            if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                drawerlayout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }

        fun onNavigationItemSelected(item: MenuItem): Boolean {

            when(item.itemId){
                R.id.nav_home ->{
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                }
                R.id.nav_map ->{
                    val intent = Intent(this, Maps::class.java)
                    startActivity(intent)
                }
                R.id.nav_observations ->{
                    val intent = Intent(this, Observations::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings ->{
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout ->{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerlayout.closeDrawer(GravityCompat.START)
            return true
    }



        val seekBar = findViewById<SeekBar>(R.id.seekBar2)
        val textView = findViewById<TextView>(R.id.textView)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress value
                textView.text = "$progress" + "km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something when the user starts sliding the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something when the user stops sliding the SeekBar
            }
    })


}
    data class SettingsData(
        val maxDistance: Int = 0,
        val measuringSystem: String = "",
        val privacyOption: String = "",
        val shareContactDetails: Boolean = false
    )
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}