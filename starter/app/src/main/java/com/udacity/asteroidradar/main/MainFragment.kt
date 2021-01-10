package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.di.ServiceLocator
import com.udacity.asteroidradar.domain.Asteroid

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ServiceLocator.provideMainViewModel(activity.application)
    }

    private val asteroidsAdapter = AsteroidsAdapter(
        AsteroidClick { navigateToDetails(it) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = asteroidsAdapter
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(
            viewLifecycleOwner,
            { asteroids -> asteroidsAdapter.submitList(asteroids) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_menu -> viewModel.filterByWeek()
            R.id.show_rent_menu -> viewModel.filterByDay()
            else -> viewModel.showAll()
        }
        return true
    }

    private fun navigateToDetails(asteroid: Asteroid) {
        findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
    }

}

class AsteroidClick(val block: (Asteroid) -> Unit) {
    /**
     * Called when an asteroid is clicked
     *
     * @param asteroid the asteroid that was clicked
     */
    fun onClick(asteroid: Asteroid) = block(asteroid)
}
