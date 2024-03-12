package com.albara.bestapptask.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.albara.bestapptask.data.objects.Movie
import com.albara.bestapptask.data.roomDB.MovieDatabase
import com.albara.bestapptask.util.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val searchResult by lazy { MutableLiveData<List<Movie>>() }
    val searchInFavoriteResult  by lazy { MutableLiveData<List<Movie>>() }
    private val sortType = MutableLiveData(SortType.ByRanking)
    val movies : LiveData<List<Movie>>
    val favoritedMovies : LiveData<List<Movie>>
    private val repository : Repository

    private val _errorMassege by lazy { MutableLiveData<String>() }
    val errorMassege : LiveData<String> get() = _errorMassege

    init {
        val dao = MovieDatabase.getInstance(application).movieDao()
        repository = Repository(dao)
        favoritedMovies = repository.getFavoritedMovies()
        movies = sortType.switchMap {
            viewModelScope.launch(Dispatchers.IO) {
                if(repository.isEmpty()) {
                    fetchMoviesFromApi()
                }
            }
            when(it){
                SortType.ByRanking -> repository.getAllSortedByRank()
                SortType.ByRating -> repository.getAllSortedByRating()
                SortType.ByAlphapet -> repository.getAllSortedByAlphapet()
                SortType.ByVotes -> repository.getAllSortedByVotes()
            }
        }
    }

    private suspend fun fetchMoviesFromApi() {
        val response = try {
            repository.fetchTopMovies()
        } catch (e : Exception) {
            _errorMassege.postValue(e.message)
            return
        }
        response.body()?.run {
            if(response.isSuccessful)
                repository.insertMovies(docs)
        } ?: _errorMassege.postValue("Что-то пошло не так")
    }

    fun addOrRemoveFromFavorite(movie : Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addOrRemoveFromFavorite(movie)
        }
    }

    fun search(searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result =  repository.search(searchQuery)
            searchResult.postValue(result)
        }
    }

    fun searchInFavorite(searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result =  repository.searchInFavorite(searchQuery)
            searchInFavoriteResult.postValue(result)
        }
    }

    fun getAllSortedByRank() {
        sortType.value = SortType.ByRanking
    }

    fun getAllSortedByAlphapet() {
        sortType.value = SortType.ByAlphapet
    }

    fun getAllSortedByRating() {
        sortType.value = SortType.ByRating
    }

    fun getAllSortedByVotes() {
        sortType.value = SortType.ByVotes
    }


}