package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.ListItemAsteroidBinding
import com.udacity.asteroidradar.domain.Asteroid

class AsteroidsAdapter(private val callback: AsteroidClick)
    : ListAdapter<Asteroid, AsteroidViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.bind(asteroid = getItem(position), callback = callback)
    }

}

class DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }
}

class AsteroidViewHolder private constructor(
    private val binding: ListItemAsteroidBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(asteroid: Asteroid, callback: AsteroidClick) {
        binding.asteroid = asteroid
        binding.asteroidCallback = callback
        binding.executePendingBindings()
    }

    companion object {
        fun create(parent: ViewGroup): AsteroidViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemAsteroidBinding.inflate(inflater, parent, false)
            return AsteroidViewHolder(binding)
        }
    }

}