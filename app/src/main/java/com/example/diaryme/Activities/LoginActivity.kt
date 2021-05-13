package com.example.diaryme.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.diaryme.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null){
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        textViewLoginRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    fun backIntent(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun login(view: View){
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val pass = findViewById<EditText>(R.id.editTextPassword).text.toString()

        if(email.isNotEmpty() && pass.isNotEmpty()){
            auth.signInWithEmailAndPassword(editTextEmail.text.toString(),editTextPassword.text.toString()).addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext,"Welcome: ${auth.currentUser?.email.toString()}",Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, FeedActivity::class.java))
                    finish()
                    }
            }.addOnFailureListener {e ->
                if (e!= null){
                    Toast.makeText(applicationContext,"Email/Password salah atau belum terdaftar!",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(applicationContext,"Email atau Password kosong!",Toast.LENGTH_LONG).show()
        }
    }
}