package com.albara.bestapptask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.albara.bestapptask.R
import com.albara.bestapptask.adapter.RvActionsListener
import com.albara.bestapptask.adapter.RvAdapter
import com.albara.bestapptask.data.objects.Movie
import com.albara.bestapptask.data.viewModel.SharedViewModel
import com.albara.bestapptask.databinding.FragmentMoviesListBinding


class MoviesListFragment : Fragment(),
    RvActionsListener, SearchView.OnQueryTextListener, Observer<List<Movie>> {

    private var _binding : FragmentMoviesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private val adapter = RvAdapter(this)
    private var inSearch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovies.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        viewModel.favoritedMovies.observe(viewLifecycleOwner) {
            if(it.isEmpty())
                binding.favoriteFragmentButton.visibility  = View.GONE
            else
                binding.favoriteFragmentButton.visibility  = View.VISIBLE
        }

        viewModel.movies.observe(viewLifecycleOwner){
            if(!inSearch)
                adapter.setMoviesList(it)
        }

        viewModel.errorMassege.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        binding.favoriteFragmentButton.setOnClickListener {
            inSearch = false
            findNavController().navigate(R.id.action_moviesList_to_favoritedMoviesFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpMenuBar()
    }

    private fun setUpMenuBar() {
        val menu = binding.tbTopmovies
        menu.inflateMenu(R.menu.top_movies_fragment_menu)

        val searchView = menu.findViewById<SearchView>(R.id.menu_search)
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnSearchClickListener {
            viewModel.searchResult.observeForever(this)
            inSearch = true
        }
        searchView?.setOnCloseListener {
            inSearch = false
            viewModel.searchResult.removeObservers(this)
            setDeafultOrder()
            false
        }

        binding.tbTopmovies.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sort_by_rank -> {
                    preformSort(1)
                    true
                }
                R.id.sort_By_alphapet -> {
                    preformSort(2)
                    true
                }
                R.id.sort_By_rating -> {
                    preformSort(3)
                    true
                }
                R.id.sort_By_votes -> {
                    preformSort(4)
                    true
                }
                else -> false
            }
        }
    }

    private fun preformSort(sortNumber : Int){
        if(inSearch){
            Toast.makeText(requireContext(), "Невозможно выполнить сортировку во время поиска"
            ,Toast.LENGTH_SHORT).show()
            return
        }
        when(sortNumber){
            1 -> viewModel.getAllSortedByRank()
            2 -> viewModel.getAllSortedByAlphapet()
            3 -> viewModel.getAllSortedByRating()
            4 -> viewModel.getAllSortedByVotes()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.tbTopmovies.menu.clear()
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
            viewModel.search(searchQuery)
        }
    }

    override fun onChanged(value: List<Movie>) {
        adapter.setMoviesList(value)
    }

    private fun setDeafultOrder(){
        viewModel.movies.value?.let {
            adapter.setMoviesList(it)
        }
    }
}