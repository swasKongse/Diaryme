package com.example.diaryme.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diaryme.Adapter.RVAdapter
import com.example.diaryme.Model.Post
import com.example.diaryme.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var adapter: RVAdapter? = null
    val posts = arrayListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = RVAdapter(posts)
        recyclerView.adapter = adapter

        getDataFromFirestore()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post) {
            //Upload activity
            startActivity(Intent(applicationContext, UploadActivity::class.java))
            finish()

        } else if (item.itemId == R.id.logout) {
            var sp = this.getSharedPreferences("com.example.photome", MODE_PRIVATE)
            sp.edit().remove("userName")
            sp.edit().remove("profilePhotos")
            //Logout
            auth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getDataFromFirestore() {
        database.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        posts.clear()
                        val documents = value.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val lokasi= document.get("lokasi") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val profilePhoto = document.get("profilePhoto") as String
                            val userName = document.get("userName") as String
                            val timestamp = document.get("date") as Timestamp
                            val date = timestamp.toDate()

                            val tempPost = Post(userName,comment,lokasi,profilePhoto,downloadUrl)
                            posts.add(tempPost)
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}