package fowlfollower.com

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlin.collections.Map

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerlayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        drawerlayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar1)

        val cardview = findViewById<CardView>(R.id.MapView)
        val cardview1 = findViewById<CardView>(R.id.cardView2)

        cardview.setOnClickListener{
            val intent = Intent(this, Maps::class.java)
            startActivity(intent)
        }

        cardview1.setOnClickListener {
            val intent = Intent(this, Observations::class.java)
            startActivity(intent)
        }

        setSupportActionBar(toolbar)
        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()


        navigationView.setNavigationItemSelectedListener (this)
        navigationView.setCheckedItem(R.id.nav_home)

    }
    @Override
    override fun onBackPressed()
    {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

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
}