package com.example.myapplication.data.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.myapplication.model.Playlist

@Dao
interface PlaylistDao {
    @Insert
    suspend fun inserir(playlist: Playlist)

    @Query("SELECT * FROM playlists")
    suspend fun buscarTodos(): List<Playlist>

    @Update
    suspend fun atualizar(playlist: Playlist)

    @Delete
    suspend fun deletar(playlist: Playlist)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun buscarPorId(id: Int): Playlist?
}
