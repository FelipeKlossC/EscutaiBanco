package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository

class MusicaViewModelFactory(
    private val musicaRepository: MusicaRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicaViewModel(musicaRepository, playlistRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
