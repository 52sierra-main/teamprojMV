package com.example.teamprojmv


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.teamprojmv.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dayTime = LocalDate.now()
        val dayWithCulture = getDayWithCulture(dayTime)
        binding.calendar.minDate = dayTime.toEpochDay() * 86400000
        binding.calendar.maxDate = dayWithCulture.toEpochDay() * 86400000
        binding.calendar.date = dayWithCulture.toEpochDay() * 86400000
        binding.calendar.setOnDateChangeListener( object :
            CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
                true
            }
        })

        val restDay = dayWithCulture.dayOfYear - dayTime.dayOfYear
        binding.dayText.text = "D - ${restDay}"
        binding.dayDescriptionText.text = "문화가 있는 날까지 \n ${restDay}일 남았습니다."

        binding.currentDateTime.text = "${dayTime.year}년 ${dayTime.monthValue}월 ${dayTime.dayOfMonth}일"
        Log.d("calendar", "${binding.calendar.date}")

        //그리드뷰 채우기
        val movieGridView: GridView = binding.gridView
        val items = generateItemList(20) // Generate a list of 20 movies
        val adapter = MyAdapter(this, items)
        movieGridView.adapter = adapter

        //그리드 클릭 리스너
        movieGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("title", selectedItem.title)
                putExtra("imageUrl", selectedItem.imageUrl)
            }
            startActivity(intent)
        }
    }
    //옆 파일에서 가져온 생성기
    private fun generateItemList(count: Int): List<GridItem> {
        val itemList = mutableListOf<GridItem>()
        for (i in 1..count) {
            itemList.add(GridItem("Title $i", "https://via.placeholder.com/150?text=Image+$i"))
        }
        return itemList
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