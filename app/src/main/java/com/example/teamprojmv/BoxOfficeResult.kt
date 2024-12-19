package com.example.teamprojmv

import com.squareup.moshi.Json


data class BoxOfficeResult(
    @Json(name = "boxofficeType") val boxofficeType: String,
    @Json(name = "showRange") val showRange: String,
    @Json(name = "dailyBoxOfficeList") val dailyBoxOfficeList: List<DailyBoxOffice>
)