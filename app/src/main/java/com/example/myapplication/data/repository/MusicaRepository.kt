package com.example.myapplication.data.repository

import com.example.myapplication.data.local.Musica
import com.example.myapplication.data.local.MusicaDao
import kotlinx.coroutines.flow.Flow

class MusicaRepository(private val musicaDao: MusicaDao) {
    fun getAllMusicas(): Flow<List<Musica>> = musicaDao.buscarTodos()

    suspend fun inserir(musica: Musica) {
        musicaDao.inserir(musica)
    }

    suspend fun atualizar(musica: Musica) {
        musicaDao.atualizar(musica)
    }

    suspend fun deletar(musica: Musica) {
        musicaDao.deletar(musica)
    }

    suspend fun buscarPorId(id: Int): Musica? {
        return musicaDao.buscarPorId(id)
    }
}
