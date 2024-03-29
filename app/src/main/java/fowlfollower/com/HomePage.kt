package fowlfollower.com

import android.content.ClipData.Item
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import fowlfollower.com.databinding.ActivityHomePageBinding
import androidx.cardview.widget.CardView;
import kotlin.collections.Map
import android.content.Intent
import android.view.MenuItem
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import fowlfollower.com.ui.gallery.GalleryFragment
import fowlfollower.com.ui.gallery.GalleryViewModel
import fowlfollower.com.ui.settings.Setting

class HomePage : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHomePage.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home_page)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val cardView2: CardView = findViewById(R.id.MapView)

        cardView2.setOnClickListener {
            // Handle card click event here
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }

        val cardView1: CardView = findViewById(R.id.cardView2)

        cardView1.setOnClickListener {
            // Handle card click event here
            val intent = Intent(this, Observations::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_page, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home_page)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


//public boolean onOptionsItemSelected(MenuItem item)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)

        {R.id.setting -> startActivity(Intent(this , Settings::class.java)) }
        return false
    }

}