package fowlfollower.com

//import androidx.synthetic.main.activity_create_observations.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID



class CreateObservations : AppCompatActivity() , View.OnClickListener {
    //private lateinit var recyclerView: RecyclerView
    //private lateinit var eventAdapter: ObservationsAdapter


    //lateinit var tv_add_image: TextView
    lateinit var iv_place_image: ImageView
    lateinit var et_date: EditText
    lateinit var toolbar_add_place: Toolbar
    lateinit var button7: Button
    lateinit var imageView: ImageView
    lateinit var btn_save: Button
    lateinit var et_title: EditText
    lateinit var et_description: EditText
    lateinit var et_location: EditText
    lateinit var database: DatabaseReference
    var isRecording = false
    private val REQUEST_PERMISSION_CODE = 100
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: FirebaseDatabase

    private lateinit var audioFileDownloadUrl: String

    companion object {
        private val REQUEST_CAMERA = 1
        private val REQUEST_GALLERY = 2
        private const val IMAGE_DIRECTORY = "Bird Observations"
    }

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_observations)


        btn_save = findViewById(R.id.btn_save)
        et_location = findViewById(R.id.et_location)
        et_title = findViewById(R.id.et_title)
        et_description = findViewById(R.id.et_description)
        iv_place_image = findViewById(R.id.iv_place_image)
        et_date = findViewById(R.id.et_date)
        toolbar_add_place = findViewById(R.id.toolbar_add_place)


        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }
        updateDateInView()
        et_date.setOnClickListener(this)
        // btn_save.setOnClickListener(this)

        val btnCamera = findViewById<Button>(R.id.button7)
        val btnGallery = findViewById<Button>(R.id.button8)

        findViewById<ImageView>(R.id.iv_place_image)
        btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }

        btnGallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }

//------------------------Audio--------------------------------------------------
       /* mediaRecorder = MediaRecorder()
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance()

        val micButton = findViewById<ImageButton>(R.id.Mic)
        var isRecording = false
        micButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (checkPermission() && !isRecording) {
                        startRecording()
                        isRecording = true
                    } else if (isRecording) {
                        stopRecording()
                        isRecording = false
                    } else {
                        requestPermission()
                    }
                    true // Consume the event
                }
                MotionEvent.ACTION_UP -> {
                    if (isRecording) {
                        stopRecording()
                        isRecording = false
                    }
                    true // Consume the event
                }
                else -> false
            }
        }*/
// ------------------------------------------------------------------------------

        btn_save.setOnClickListener {


            val imageUrl = saveImageToInternalStorage(iv_place_image.drawable.toBitmap()).toString()
            val title = et_title.text.toString()
            val description = et_description.text.toString()
            val date = et_date.text.toString()
            val location = et_location.text.toString()

            val latLngRegex = Regex("Lat: (.*), Lon: (.*)")
            val matchResult = latLngRegex.find(location)
            val latitude = matchResult?.groupValues?.get(1)?.toDouble() ?: 0.0
            val longitude = matchResult?.groupValues?.get(2)?.toDouble() ?: 0.0

            //val audioFileUri = Uri.fromFile(File(externalCacheDir?.absolutePath, "${UUID.randomUUID()}.3gp"))

            val database = FirebaseDatabase.getInstance()
            val observationsRef =
                database.getReference("Bird Observations") // "observations" is the name of your database node


            // Create a new Observation object with the data
            val observation = Observation(title, description, date, location, latitude, longitude,imageUrl)

            // Push the data to Firebase database
            val observationId =
                observationsRef.push().key // Generates a unique key for the observation
            observationId?.let {
                observationsRef.child(it).setValue(observation)
                    .addOnSuccessListener {
                        // Data successfully saved to Firebase
                        Toast.makeText(this, "Observation Saved", Toast.LENGTH_SHORT)
                            .show()
                        // Redirect to your desired activity
                        val intent = Intent(this, Observations::class.java)
                        startActivity(intent)
                        finish() // Close the current activity if needed
                    }
                    .addOnFailureListener {
                        // Failed to save data to Firebase
                        Toast.makeText(
                            this,
                            "Failed to save observation to Firebase!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        val sharedPreferences = getSharedPreferences("LocationData", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat("latitude", 0f).toDouble()
        val longitude = sharedPreferences.getFloat("longitude", 0f).toDouble()

        if (latitude != 0.0 && longitude != 0.0) {
            et_location.setText("Lat: $latitude, Lon: $longitude")
        }

    }


    data class Observation(
        val title: String = "",
        val description: String = "",
        val date: String = "",
        val location: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val imageUrl: String = ""
        //val audioFileUri: String = ""
    )

    /*private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            REQUEST_PERMISSION_CODE
        )
    }

    private var audioFile: File? = null

    private fun startRecording() {
        val fileName = "${UUID.randomUUID()}.3gp"
        audioFile = File(externalCacheDir?.absolutePath, fileName)

        mediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile!!.absolutePath)
            prepare()
            start()
        }
    }



private fun stopRecording() {
    mediaRecorder.stop()
    mediaRecorder.release()
    isRecording = false

    // You may want to update the UI to indicate that recording has stopped.

    // Save the audio file to Firebase Storage
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val audioRef = storageRef.child("audio/${UUID.randomUUID()}.3gp")

    val uploadTask = audioRef.putFile(Uri.fromFile(audioFile))

    uploadTask.addOnSuccessListener { taskSnapshot ->
        // Audio file uploaded successfully, get the download URL
        audioRef.downloadUrl.addOnSuccessListener { uri ->
            // Save the download URL to the Realtime Database or use it as needed
            audioFileDownloadUrl = uri.toString()
        }
    }.addOnFailureListener {
        // Handle failure to upload audio file
    }
}*/
    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault()) // A date format
        et_date.setText(
            sdf.format(cal.time).toString()
        ) // A selected date using format which we have used is set to the UI.
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        iv_place_image!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@CreateObservations, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap // Bitmap from camera

                saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                iv_place_image!!.setImageBitmap(thumbnail) // Set to the imageView.
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@CreateObservations,
                    dateSetListener, // This is the variable which have created globally and initialized in setupUI method.
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR), // Here the cal instance is created globally and used everywhere in the class where it is required.
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }


    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }
}
