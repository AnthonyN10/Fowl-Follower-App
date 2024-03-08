package fowlfollower.com

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class ExpandedCardView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expanded_card_view)

        val toolbar_add_place = findViewById<Toolbar>(R.id.toolbar_add_place)

        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val location = intent.getStringExtra("location")
        val imageUrl = intent.getStringExtra("imageUrl")
        //val audioFileUrl = intent.getStringExtra("audioFileUrl")


        // For example:
        val titleTextView: TextView = findViewById(R.id.textViewTitle)
        titleTextView.text = title

        val titleTextDescription: TextView = findViewById(R.id.textViewDescription)
        titleTextDescription.text = description

        val titleTextDate: TextView = findViewById(R.id.textViewDate)
        titleTextDate.text = date

        val titleTextLocation: TextView = findViewById(R.id.textViewLocation)
        titleTextLocation.text = location

        val imageTextView: ImageView = findViewById(R.id.imageViewObservation)
        //imageTextView.text = imageUrl

        Glide.with(this)
            .load(imageUrl)
            .into(imageTextView)

        // Find your play button by its ID

        /*val audioFileUriString = intent.getStringExtra("audioFileUri")
        val audioFileUri = Uri.parse(audioFileUriString)

        // Find your play button by its ID
        val playButton: ImageButton = findViewById(R.id.play)
        playButton.setOnClickListener {
            startPlaying(audioFileUri)
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    private fun startPlaying(audioFileUri: Uri) {
        Log.d("ExpandedCardView", "Received audioFileUri: $audioFileUri")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFileUri.toString())
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release the MediaPlayer when the activity is destroyed
    }*/
    }
}

