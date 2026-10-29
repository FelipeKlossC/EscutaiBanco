package com.example.myapplication.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun inserir(playlist: Playlist)

    @Query("SELECT * FROM playlists")
    fun buscarTodos(): Flow<List<Playlist>>

    @Update
    suspend fun atualizar(playlist: Playlist)

    @Delete
    suspend fun deletar(playlist: Playlist)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun buscarPorId(id: Int): Playlist?
}
