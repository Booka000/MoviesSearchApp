package com.albara.bestapptask.data.viewModel

import androidx.lifecycle.LiveData
import com.albara.bestapptask.data.objects.Movie
import com.albara.bestapptask.data.objects.TopMoviesResponse
import com.albara.bestapptask.data.retrofit.RetrofitInstance
import com.albara.bestapptask.data.roomDB.MovieDao
import retrofit2.Response
import retrofit2.HttpException
import java.io.IOException

class Repository(private val movieDao: MovieDao) {

    suspend fun fetchTopMovies(): Response<TopMoviesResponse> {
        try {
            return RetrofitInstance.services.getTopMovies()
        } catch (e : IOException) {
            throw Exception("Нет соединения с интернетом")
        } catch (e : HttpException) {
            throw Exception("Неожиданный ответ")
        }
    }

    suspend fun insertMovies(movies :List<Movie>){
        movieDao.insertMovies(movies)
    }

    suspend fun addOrRemoveFromFavorite(movie : Movie) {
        movieDao.addOrRemoveFromFavorite(movie)
    }

    fun getFavoritedMovies() : LiveData<List<Movie>> {
        return movieDao.getAllFavorited()
    }

    suspend fun search(searchQuery : String) : List<Movie> {
        return movieDao.search(searchQuery)
    }

    suspend fun searchInFavorite(searchQuery : String) : List<Movie> {
        return movieDao.searchInFavorite(searchQuery)
    }
     suspend fun isEmpty() : Boolean {
         return movieDao.isEmpty()
     }

    fun getAllSortedByRank() : LiveData<List<Movie>> {
        return movieDao.getAllSortedByRanking()
    }

    fun getAllSortedByAlphapet() : LiveData<List<Movie>> {
        return movieDao.getAllSortedByAlphabet()
    }


    fun getAllSortedByRating() : LiveData<List<Movie>> {
        return movieDao.getAllSortedByRating()
    }


    fun getAllSortedByVotes() : LiveData<List<Movie>> {
        return movieDao.getAllSortedByVotes()
    }
}