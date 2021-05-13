package com.example.diaryme.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaryme.R
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.util.*

class UploadActivity : AppCompatActivity() {
    private lateinit var selectedImgUri: Uri
    private lateinit var selectedBitmap: Bitmap
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var textloc: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        textloc = findViewById(R.id.TextLocation)
    }

    fun uploadImg(view: View) {
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

    fun addLocation(view: View) {
        try {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), 3)
        } catch (ignored: GooglePlayServicesNotAvailableException) {
        } catch (ignored: GooglePlayServicesRepairableException) {
        }
    }

    fun upload(view: View) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference.child("uploadImages").child(imageName)

        if (selectedImgUri != null) {
            reference.putFile(selectedImgUri).addOnSuccessListener { taskSnapShot ->
                val uploadedPictureRef =
                    FirebaseStorage.getInstance().reference.child("uploadImages").child(
                        imageName
                    )
                uploadedPictureRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println(downloadUrl)
                    var sp = this.getSharedPreferences(
                        "com.example.photome",
                        MODE_PRIVATE
                    )
                    var userName = sp.getString("userName", "No Name")
                    var profilePhoto = sp.getString("profilePhotos", " ")

                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", auth.currentUser!!.email.toString())
                    postMap.put("comment", editTextComment.text.toString())
                    postMap.put("lokasi",TextLocation.text.toString())
                    postMap.put("date", Timestamp.now())
                    postMap.put("userName", userName!!)
                    postMap.put("profilePhoto", profilePhoto!!)

                    database.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if (task.isComplete && task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
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
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            2 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    selectedImgUri = data.data!!

                    try {
                        if (selectedImgUri != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                val source = ImageDecoder.createSource(
                                    this.contentResolver,
                                    selectedImgUri
                                )
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    this.contentResolver,
                                    selectedImgUri
                                )
                            }
                        } else {
                            selectedBitmap = BitmapFactory.decodeResource(
                                this.resources,
                                R.drawable.blank_profile
                            )
                        }
                        uploadImg.setImageBitmap(selectedBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            3 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = PlacePicker.getPlace(data, this)
                    val geocoder = Geocoder(this, Locale.getDefault())
                    var addresses: MutableList<Address>? = null
                    try {
                        addresses = geocoder.getFromLocation(
                            place.latLng.latitude,
                            place.latLng.longitude,
                            1
                        )
                    } catch (ioException: Exception) {
                        Log.e("", "Error in getting address for the location")
                    }
                    val address = addresses!![0]
                    val addressDetails = StringBuffer()
                    addressDetails.append(address.featureName)
                    addressDetails.append(",")
                    addressDetails.append(address.locality)
                    addressDetails.append(",")
                    addressDetails.append(address.subAdminArea)
                    addressDetails.append(",")
                    addressDetails.append(address.adminArea)
                    addressDetails.append(",")
                    addressDetails.append(address.countryName)
                    addressDetails.append(",")
                    addressDetails.append(address.postalCode)
                    addressDetails.append("\n")
                    textloc!!.text = addressDetails.toString()
                }
            }
        }
    }
}