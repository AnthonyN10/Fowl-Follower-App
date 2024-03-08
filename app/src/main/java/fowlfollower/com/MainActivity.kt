package fowlfollower.com

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import fowlfollower.com.ui.settings.Setting
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        firebaseAuth = FirebaseAuth.getInstance()

        val usernametxt = findViewById<TextView>(R.id.editTextText)
        val passwordtxt = findViewById<TextView>(R.id.editTextTextPassword)

        val buttonRedirect1 = findViewById<Button>(R.id.button3)
        buttonRedirect1.setOnClickListener {
            val intent = Intent(this, SIgnUp::class.java)
            startActivity(intent)
        }

        val buttonRedirect2 = findViewById<Button>(R.id.button2)
        buttonRedirect2.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        val Loginbtn = findViewById<Button>(R.id.btn_login)
        Loginbtn.setOnClickListener {

            val username = usernametxt.text.toString().trim()
            val password = passwordtxt.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity2::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                    if (username.isEmpty() && password.isEmpty())
                        Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
                    usernametxt.requestFocus()
                }
            }



        }
    }
}

