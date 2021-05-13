package com.example.diaryme.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaryme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.Exception
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImgUri: Uri
    private lateinit var selectedBitmap: Bitmap
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()

        imageViewRegisterBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun register(view: View) {
        val email = editTextEmailR.text.toString()
        val password = editTextPasswordR.text.toString()
        val username: String = editTextUsernameR.text.toString()

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        storage = FirebaseStorage.getInstance()
        val reference = storage.reference.child("profilePhotos").child(imageName)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var downloadUrl = ""
                if (selectedImgUri != null) {
                    reference.putFile(selectedImgUri).addOnSuccessListener { taskSnapShot ->
                        val uploadedPictureRef =
                            FirebaseStorage.getInstance().reference.child("profilePhotos")
                                .child(imageName)
                        uploadedPictureRef.downloadUrl.addOnSuccessListener { uri ->
                            downloadUrl = uri.toString()
                            println(downloadUrl)

                            var sp = this.getSharedPreferences("com.example.diaryme",
                                MODE_PRIVATE
                            )
                            sp.edit().putString("userName",username).apply()
                            sp.edit().putString("profilePhotos",downloadUrl).apply()
                        }
                    }
                }
                val intent = Intent(applicationContext, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun pickImg(view: View) {
        println("Function works")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ), 2
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ), 2
                )
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                selectedImgUri = data.data!!

                try {
                    if (selectedImgUri != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            val source =
                                ImageDecoder.createSource(this.contentResolver, selectedImgUri)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedImgUri
                            )
                        }
                    } else {
                        selectedBitmap =
                            BitmapFactory.decodeResource(this.resources, R.drawable.blank_profile)
                    }
                    imageViewProfile.setImageBitmap(selectedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}