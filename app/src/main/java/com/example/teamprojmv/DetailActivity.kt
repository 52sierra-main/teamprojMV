package com.example.teamprojmv

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // detail info?
        val title = intent.getStringExtra("title") ?: "Unknown Title"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // Back
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = title

        // Reference Views
        val titleView: TextView = findViewById(R.id.movieTitle)
        val overview: TextView = findViewById(R.id.movieOverview)
        val poster: ImageView = findViewById(R.id.moviePoster)

        // Set Movie Details
        titleView.text = title
        overview.text = "임시 $title."
        Glide.with(this).load(imageUrl.toUri()).into(poster)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the current activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}