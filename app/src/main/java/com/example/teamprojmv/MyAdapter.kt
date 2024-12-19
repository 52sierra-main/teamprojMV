package com.example.teamprojmv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide

import android.widget.BaseAdapter

class MyAdapter(private val context: Context, private val items: List<GridItem>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_main, parent, false)

        val titleTextView: TextView = view.findViewById(R.id.itemTitle)
        val imageView: ImageView = view.findViewById(R.id.itemImage)

        val gridItem = items[position]

        titleTextView.text = gridItem.title
        Glide.with(context)
            .load(gridItem.imageUrl) // 이미지 URL
            .placeholder(R.drawable.ic_movie_placeholder) // 로딩 중 기본 이미지
            .error(R.drawable.ic_movie_placeholder) // 로드 실패 시 기본 이미지
            .into(imageView)

        return view
    }
}