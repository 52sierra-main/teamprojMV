package com.example.teamprojmv


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.teamprojmv.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.mutableListOf

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiKey = "62b5b6731f5435fee627106b33ee37bc"
        val yesterday = getYesterdayDate()

        val dayTime = LocalDate.now()
        val dayWithCulture = getDayWithCulture(dayTime)

        // CalendarView 설정
        binding.calendar.minDate = dayTime.toEpochDay() * 86400000
        binding.calendar.maxDate = dayWithCulture.toEpochDay() * 86400000
        binding.calendar.date = dayWithCulture.toEpochDay() * 86400000
        binding.calendar.setOnDateChangeListener( object :
            CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
                true
            }
        })

        // 남은 날짜 계산
        val restDay = dayWithCulture.dayOfYear - dayTime.dayOfYear
        binding.dayText.text = "D - ${restDay}"
        binding.dayDescriptionText.text = "문화가 있는 날까지 \n ${restDay}일 남았습니다."

        binding.currentDateTime.text = "${dayTime.year}년 ${dayTime.monthValue}월 ${dayTime.dayOfMonth}일"
        Log.d("calendar", "${binding.calendar.date}")

        // 영화 데이터 가져오기
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getDailyBoxOffice(apiKey, yesterday)
                val movies = response.boxOfficeResult.dailyBoxOfficeList
                Log.d("BoxOffice", "Movies Loaded: ${movies.size}")

                val items = movies.map { movie ->
                    GridItem(movie.movieNm, "https://via.placeholder.com/150?text=${movie.movieNm}")
                }

                // GridView 채우기
                val movieGridView: GridView = binding.gridView
                val adapter = MyAdapter(this@MainActivity, items)
                movieGridView.adapter = adapter

                // GridView 클릭 리스너
                movieGridView.setOnItemClickListener { _, _, position, _ ->
                    val selectedItem = items[position]
                    val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                        putExtra("title", selectedItem.title)
                        putExtra("imageUrl", selectedItem.imageUrl)
                    }
                    startActivity(intent)
                }
            } catch (e: HttpException) {
                Log.e("BoxOffice", "HTTP Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("BoxOffice", "Error: ${e.message}")
            }
        }



    }
    // 어제 날짜 가져오기
    private fun getYesterdayDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayWithCulture(day : LocalDate) : LocalDate {
    val daysOfMonths = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    //현재 날짜에서 달을 입력받아 해당 달의 마지막 날의 요일을 구한다.
    var dayWithCulture = LocalDate.of(day.year, day.month, daysOfMonths[day.monthValue - 1])

    //만약 수요일이 아닐 경우 수요일이 될 때까지 -1 한다.
    while(dayWithCulture.dayOfWeek.value != 3){
        dayWithCulture = dayWithCulture.minusDays(1)
    }

    //만약 문화가 있는 날보다 현재 시간이 더 앞서는 경우 다음 달로 문화가 있는 날을 계산한다.
    if(day.isAfter(dayWithCulture)){
        getDayWithCulture(LocalDate.of(day.year, day.month.plus(1), 1))
    }
    return dayWithCulture
}