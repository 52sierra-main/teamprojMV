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

    private val apiKey = "10ef2c98522e79ec9dc735237f02facf" // Replace with your actual KOBIS API key

    private fun setTextOrDefault(view: TextView, text: String?) {
        // If the text is null or empty, retain the default text from the XML
        view.text = text?.takeIf { it.isNotEmpty() } ?: view.text
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val movieCd = intent.getStringExtra("movieCd") ?: "Unknown Movie Code"

        // 툴바
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Movie Details"

        // 뷰
        val titleView: TextView = findViewById(R.id.movieTitle)
        val overview: TextView = findViewById(R.id.movieOverview)
        val poster: ImageView = findViewById(R.id.moviePoster)
        val yearView: TextView = findViewById(R.id.movieYear)
        val genreView: TextView = findViewById(R.id.movieGenre)
        val distributorsView: TextView = findViewById(R.id.movieDistributors)
        val directorView: TextView = findViewById(R.id.director)
        val actorsView: TextView = findViewById(R.id.actors)
        val keywordsView: TextView = findViewById(R.id.keywords)

        // 코비스api
        lifecycleScope.launch {
            val movieDetails = fetchMovieDetails(movieCd)
            movieDetails?.let {
                withContext(Dispatchers.Main) {
                    setTextOrDefault(titleView, it.movieNm)
                    yearView.text = "출시: ${it.openDt.substring(0, 4)}년"
                    setTextOrDefault(genreView, it.genreAlt)
                    setTextOrDefault(overview, it.synopsis)
                    setTextOrDefault(distributorsView, it.companyAlt)
                    setTextOrDefault(directorView, "감독: ${it.directorNm}")
                    setTextOrDefault(actorsView, it.actorAlt)
                    setTextOrDefault(keywordsView, it.keywords)

                    Glide.with(this@DetailActivity)
                        .load(it.posterUrl)
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .into(poster)
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

    private suspend fun fetchMovieDetails(movieCd: String): MovieDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    URL("https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?key=$apiKey&movieCd=$movieCd")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)
                    val movieInfoResult = jsonObject.getJSONObject("movieInfoResult")
                    val movieInfo = movieInfoResult.getJSONObject("movieInfo")
                    val genres = movieInfo.getJSONArray("genres").let { array ->
                        (0 until array.length()).joinToString(", ") { index ->
                            array.getJSONObject(index).optString("genreNm")
                        }
                    }
                    val companys = movieInfo.getJSONArray("companys").let { array ->
                        (0 until array.length()).map { index ->
                            array.getJSONObject(index).optString("companyNm").trim()
                        }.filter { it.isNotEmpty() } // Remove blanks
                            .distinct() // Remove duplicates
                            .joinToString(", ") // Join with commas
                    }




                    return@withContext MovieDetails(
                        movieNm = movieInfo.optString("movieNm"),
                        openDt = movieInfo.optString("openDt"),
                        genreAlt = genres,
                        synopsis = movieInfo.optString("synopsis"),
                        companyAlt = companys,
                        directorNm = movieInfo.getJSONArray("directors").optJSONObject(0)
                            ?.optString("peopleNm") ?: "Unknown Director",
                        actorAlt = movieInfo.getJSONArray("actors").let { array ->
                            (0 until array.length()).joinToString(", ") { index ->
                                array.getJSONObject(index).optString("peopleNm")
                            }
                        },
                        keywords = movieInfo.optString("typeNm"),
                        posterUrl = "https://via.placeholder.com/150?text=${movieInfo.optString("movieNm")}"
                    )
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    data class MovieDetails(
        val movieNm: String,
        val openDt: String,
        val genreAlt: String?,
        val synopsis: String?,
        val companyAlt: String?,
        val directorNm: String,
        val actorAlt: String,
        val keywords: String?,
        val posterUrl: String
    )
}
