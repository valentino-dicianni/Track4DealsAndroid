package com.example.track4deals.ui.offers.recyclerView


import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.track4deals.MainActivity
import com.example.track4deals.R
import kotlinx.android.synthetic.main.full_screen_layout.*


class FullScreenImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //actionbar
        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.largeImageTitle)
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.full_screen_layout)
        val callingActivityIntent = intent
        if (callingActivityIntent != null) {
            val imageUri = intent.getStringExtra("url")

            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .into(fullScreenImageView)
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val mainActivity = Intent( this, MainActivity::class.java)
        startActivity(mainActivity)
    }

}