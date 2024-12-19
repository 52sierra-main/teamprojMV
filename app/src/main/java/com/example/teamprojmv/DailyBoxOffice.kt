package com.example.teamprojmv

import com.squareup.moshi.Json
import java.io.Serial

data class DailyBoxOffice(
    @Json(name = "rnum")val rnum: Int = 0,
    @Json(name = "rank")val rank: Int = 0,
    @Json(name = "rankInten")val rankInten: Int = 0,
    @Json(name = "rankOldAndNew")val rankOldAndNew: String = "OLD",
    @Json(name = "movieCd")val movieCd: String = "",
    @Json(name = "movieNm")val movieNm: String = "",
    @Json(name = "openDt")val openDt: String = "",

    @Json(name = "salesAcc")val salesAcc: Long = 0,
    @Json(name = "salesAmt")val salesAmt: Long = 0,
    @Json(name = "salesChange")val salesChange: Double? = null,
    @Json(name = "salesInten")val salesInten: Int = 0,
    @Json(name = "salesShare")val salesShare: Double? = null,

    @Json(name = "audiCnt")val audiCnt: Int = 0,
    @Json(name = "audiInten")val audiInten: Int = 0,
    @Json(name = "audiChange")val audiChange: Double? = null,
    @Json(name = "audiAcc")val audiAcc: Long = 0,

    @Json(name = "scrnCnt")val scrnCnt: Int = 0,
    @Json(name = "showCnt")val showCnt: Int = 0



)


