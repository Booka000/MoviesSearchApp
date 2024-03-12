package com.albara.bestapptask.data.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.albara.bestapptask.data.objects.Movie

@Dao
interface MovieDao {
    @Insert
    suspend fun insertMovies(movies : List<Movie>)

    @Update
    suspend fun addOrRemoveFromFavorite(movie: Movie)

    @Update
    suspend fun removeFromFavorite(movies : List<Movie>)

    @Query("SELECT (SELECT COUNT(*) FROM movie_table) == 0")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM movie_table ORDER BY imdb DESC")
    fun getAllSortedByRating() : LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table ORDER BY name ASC")
    fun getAllSortedByAlphabet() : LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table ORDER BY votes DESC")
    fun getAllSortedByVotes() : LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table ORDER BY votes DESC, imdb DESC")
    fun getAllSortedByRanking(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE isFavorited = 1")
    fun getAllFavorited(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE name LIKE :searchQuery")
    suspend fun search(searchQuery : String) : List<Movie>

    @Query("SELECT * FROM movie_table WHERE name LIKE :searchQuery AND isFavorited = 1")
    suspend fun searchInFavorite(searchQuery : String) : List<Movie>
}