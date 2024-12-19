package com.example.teamprojmv

import retrofit2.http.GET
import retrofit2.http.Query

interface BoxOfficeApi {
    @GET("kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    suspend fun getDailyBoxOffice(
        @Query("key") apiKey: String,
        @Query("targetDt") targetDate: String
    ): BoxOfficeForm
}
