package fowlfollower.com

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import fowlfollower.com.databinding.ActivitySignUpBinding

class SIgnUp : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater) //Connection to database do not touch
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val SignName = findViewById<TextView>(R.id.editTextText2)
        val SignSname = findViewById<TextView>(R.id.editTextText2)
        val SignEmail = findViewById<TextView>(R.id.editTextText4)
        val SignPass = findViewById<TextView>(R.id.editTextTextPassword2)

        val buttonRedirect = findViewById<Button>(R.id.button)
        binding.button.setOnClickListener{
            val email = binding.editTextText4.text.toString().trim() //.binding is to say fill database here
            val password = binding.editTextTextPassword2.text.toString().trim()


        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    finish()
                    }
                    else  {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
                }


            val button1 = findViewById<ImageButton>(R.id.googlebtn)
            button1.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
