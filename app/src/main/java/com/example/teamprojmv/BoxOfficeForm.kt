package com.example.teamprojmv

import com.squareup.moshi.Json

data class BoxOfficeForm(
    @Json(name = "boxofficeResult")  val boxOfficeResult: BoxOfficeResult
)