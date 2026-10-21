package com.example.myapplication.data.db

import androidx.room.*
import com.example.myapplication.model.Playlist

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(playlist: Playlist): Long

    @Query("SELECT * FROM playlists")
    suspend fun buscarTodos(): List<Playlist>

    @Update
    suspend fun atualizar(playlist: Playlist): Int

    @Delete
    suspend fun deletar(playlist: Playlist): Int

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun buscarPorId(id: Int): Playlist?
}
