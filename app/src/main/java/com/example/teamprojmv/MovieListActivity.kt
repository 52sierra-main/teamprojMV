package com.example.teamprojmv

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamprojmv.databinding.ActivityMovieListBinding

class MovieListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        val gridView: GridView = findViewById(R.id.gridView)

        // 임의의 리스트 생성
        val itemCount = 20 // 생성할 리스트의 수 (필요에 따라 변경 가능)
        val items = generateItemList(itemCount)

        // 아이템 리스트를 동적으로 생성하는 함수



        // 어댑터 설정
        val adapter = MyAdapter(this, items)
        gridView.adapter = adapter
        Log.d("MovieListActivity", "Items: ${items.size}")
    }
    private fun generateItemList(count: Int): List<GridItem> {
        val itemList = mutableListOf<GridItem>()
        for (i in 1..count) {
            itemList.add(GridItem("Title $i", "https://via.placeholder.com/150?text=Image+$i"))
        }
        return itemList
    }
}
