package com.example.diaryme.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.diaryme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.feed_row.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val backpress : Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = Firebase.auth.currentUser
        if (user != null) {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        textViewMainRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitProcess(-1)
    }

    fun goToLogin(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}