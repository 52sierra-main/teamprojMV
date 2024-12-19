package com.example.teamprojmv

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DetailActivity : AppCompatActivity() {

    private val apiKey = "62b5b6731f5435fee627106b33ee37bc" // 더미

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra("title") ?: "Unknown Title"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // 툴바
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = title

        // 채울 뷰
        val titleView: TextView = findViewById(R.id.movieTitle)
        val overview: TextView = findViewById(R.id.movieOverview)
        val poster: ImageView = findViewById(R.id.moviePoster)
        val yearView: TextView = findViewById(R.id.movieYear)
        val genreView: TextView = findViewById(R.id.movieGenre)
        val distributorsView: TextView = findViewById(R.id.movieDistributors)
        val directorView: TextView = findViewById(R.id.director)
        val actorsView: TextView = findViewById(R.id.actors)
        val keywordsView: TextView = findViewById(R.id.keywords)

        // Set initial data
        titleView.text = title
        Glide.with(this).load(imageUrl).into(poster)

        // Fetch movie details from the KMDb API
        lifecycleScope.launch {
            val movieDetails = fetchMovieDetails(title)
            movieDetails?.let {
                withContext(Dispatchers.Main) {
                    yearView.text = "출시: ${it.releaseYear}년"
                    genreView.text = "장르: ${it.genre}"
                    overview.text = it.plot
                    distributorsView.text = it.company
                    directorView.text = "감독: ${it.director}"
                    actorsView.text = it.actor
                    keywordsView.text = it.keyword
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun fetchMovieDetails(title: String): MovieDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val url =
                    URL("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&ServiceKey=$apiKey&title=$encodedTitle")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)
                    val dataArray = jsonObject.getJSONArray("Data")
                    if (dataArray.length() > 0) {
                        val resultArray = dataArray.getJSONObject(0).getJSONArray("Result")
                        if (resultArray.length() > 0) {
                            val movieJson = resultArray.getJSONObject(0)
                            return@withContext MovieDetails(
                                releaseYear = movieJson.optString("releaseDts").take(4),
                                genre = movieJson.optString("genre"),
                                plot = movieJson.optString("plot"),
                                company = movieJson.optString("company"),
                                director = movieJson.optString("director"),
                                actor = movieJson.optString("actor"),
                                keyword = movieJson.optString("keyword")
                            )
                        }
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    data class MovieDetails(
        val releaseYear: String,
        val genre: String,
        val plot: String,
        val company: String,
        val director: String,
        val actor: String,
        val keyword: String
    )
}
