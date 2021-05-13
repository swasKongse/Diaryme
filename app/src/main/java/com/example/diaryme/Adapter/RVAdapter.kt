package com.example.diaryme.Adapter

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryme.Activities.LocationShow
import com.example.diaryme.Model.Post
import com.example.diaryme.R
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso


class RVAdapter(
    private val postsArrayList: ArrayList<Post>
) : RecyclerView.Adapter<RVAdapter.PostHolder>() {
    class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recyclerViewUserNameText: TextView? = null
        var recyclerViewCommentText: TextView? = null
        var recyclerViewLokasiText: TextView? = null
        var profilePhoto: ImageView? = null
        var uploadPhoto: ImageView? = null
        var likeButton : Button? = null

        init {
            recyclerViewUserNameText = view.findViewById(R.id.textViewUsername)
            recyclerViewLokasiText = view.findViewById(R.id.textLocation)
            recyclerViewCommentText = view.findViewById(R.id.textViewComment)
            profilePhoto = view.findViewById(R.id.profile_photo)
            uploadPhoto = view.findViewById(R.id.imageViewPicture)
            likeButton = view.findViewById(R.id.likeButton)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.feed_row, parent, false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postsArrayList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val latitude : Double? = null
        holder.recyclerViewUserNameText?.text = postsArrayList[position].userName
        holder.recyclerViewCommentText?.text = postsArrayList[position].comment
        holder.recyclerViewLokasiText?.text = postsArrayList[position].lokasi
        Picasso.get().load(postsArrayList[position].profilePhoto).into(holder.profilePhoto)
        Picasso.get().load(postsArrayList[position].uploadImg).into(holder.uploadPhoto)
        var isClicked : Boolean = false
        holder.likeButton?.setOnClickListener { it ->
            if (!isClicked) {
                holder.likeButton!!.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                isClicked = true
            } else {
                holder.likeButton!!.setBackgroundResource(R.drawable.ic_baseline_favorite_24_shadow)
                isClicked = false
            }
        }

        holder.recyclerViewLokasiText?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val title = v.findViewById<TextView>(R.id.textLocation).text.toString()

                val coder = Geocoder(v.context)
                val address: List<Address>

                address = coder.getFromLocationName(title, 5);
                val location = address.get(0)
                val latitude = location.getLatitude()
                val longitude = location.getLongitude()
                Toast.makeText(v.context, "Directing..", Toast.LENGTH_SHORT).show()

                val startActivity = Intent()
                startActivity.setClass(v.context, LocationShow::class.java)
                startActivity.action = LocationShow::class.java.getName()
                startActivity.flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity.putExtra("latitude", latitude)
                startActivity.putExtra("longitude", longitude)
                (v.context.applicationContext).startActivity(startActivity)
            }
        })
    }
}