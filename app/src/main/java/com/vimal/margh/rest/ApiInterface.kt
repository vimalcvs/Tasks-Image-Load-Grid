package com.vimal.margh.rest

import com.vimal.margh.callback.CallbackWallpaper
import com.vimal.margh.util.Config.REST_API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {
    @Headers(CACHE, AGENT)
    @GET("api/?key=$REST_API_KEY")
    fun getWallpapers(
        @Query("category") category: String?,
        @Query("orientation") orientation: String?,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Call<CallbackWallpaper?>?

    companion object {
        const val CACHE: String = "Cache-Control: max-age=640000"
        const val AGENT: String = "User-Agent: Police-Exam-App"
    }
}
