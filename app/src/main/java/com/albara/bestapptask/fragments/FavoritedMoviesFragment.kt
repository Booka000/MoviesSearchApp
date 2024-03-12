package com.albara.bestapptask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.albara.bestapptask.R
import com.albara.bestapptask.adapter.RvActionsListener
import com.albara.bestapptask.adapter.RvAdapter
import com.albara.bestapptask.data.objects.Movie
import com.albara.bestapptask.data.viewModel.SharedViewModel
import com.albara.bestapptask.databinding.FragmentFavoritedMoviesBinding


class FavoritedMoviesFragment : Fragment(),
    RvActionsListener, SearchView.OnQueryTextListener, Observer<List<Movie>> {

    private var _binding : FragmentFavoritedMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private val  adapter = RvAdapter(this)
    private var inSearch = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritedMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFavoritedMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoritedMovies.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        viewModel.favoritedMovies.observe(viewLifecycleOwner) {
            if(!inSearch)
                adapter.setMoviesList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpMenu()
    }

    private fun setUpMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_movies_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search?.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@FavoritedMoviesFragment)
                searchView?.setOnSearchClickListener {
                    viewModel.searchInFavoriteResult.observeForever(this@FavoritedMoviesFragment)
                    inSearch = true
                }
                searchView?.setOnCloseListener {
                    inSearch = false
                    viewModel.searchInFavoriteResult.removeObservers(this@FavoritedMoviesFragment)
                    setDeafultOrder()
                    false
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.menu_search)
                    return true
                return false
            }

        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(movie: Movie) {
    }

    override fun onFavoriteCheckedChange(movie: Movie) {
        viewModel.addOrRemoveFromFavorite(movie)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.run {
            search(this)
        }
        return true
    }

    private fun search(query: String) {
        if(query.isEmpty()){
            setDeafultOrder()
        } else {
            val searchQuery = "%$query%"
            viewModel.searchInFavorite(searchQuery)
        }
    }

    override fun onChanged(value: List<Movie>) {
        adapter.setMoviesList(value)
    }

    private fun setDeafultOrder(){
        viewModel.favoritedMovies.value?.let {
            adapter.setMoviesList(it)
        }
    }
}