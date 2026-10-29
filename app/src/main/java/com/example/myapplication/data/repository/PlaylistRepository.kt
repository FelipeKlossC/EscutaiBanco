package com.example.myapplication.data.repository

import com.example.myapplication.data.local.Playlist
import com.example.myapplication.data.local.PlaylistDao
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.buscarTodos()

    suspend fun inserir(playlist: Playlist) {
        playlistDao.inserir(playlist)
    }

    suspend fun atualizar(playlist: Playlist) {
        playlistDao.atualizar(playlist)
    }

    suspend fun deletar(playlist: Playlist) {
        playlistDao.deletar(playlist)
    }

    suspend fun buscarPorId(id: Int): Playlist? {
        return playlistDao.buscarPorId(id)
    }
}
