package com.albara.bestapptask.data.retrofit

import com.albara.bestapptask.data.objects.TopMoviesResponse
import com.albara.bestapptask.util.Constants.Companion.TOKEN
import retrofit2.Response
import retrofit2.http.GET

interface KinoPoiskApiServices {
    @GET("/v1.3/movie?selectFields=id&selectFields=name&selectFields=poster.previewUrl&selectFields=rating.imdb" +
            "&selectFields=votes.imdb&sortField=votes.imdb&page=1&limit=100&type=movie&token=$TOKEN")
    suspend fun getTopMovies() : Response<TopMoviesResponse>
}