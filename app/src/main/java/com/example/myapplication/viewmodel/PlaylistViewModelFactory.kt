package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository

class PlaylistViewModelFactory(
    private val playlistRepository: PlaylistRepository,
    private val musicaRepository: MusicaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistViewModel(playlistRepository, musicaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
