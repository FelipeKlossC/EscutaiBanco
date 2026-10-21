package com.example.myapplication.data.db

import androidx.room.*
import com.example.myapplication.model.Musica

@Dao
interface MusicaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(musica: Musica): Long

    @Query("SELECT * FROM musicas")
    suspend fun buscarTodos(): List<Musica>

    @Update
    suspend fun atualizar(musica: Musica): Int

    @Delete
    suspend fun deletar(musica: Musica): Int

    @Query("SELECT * FROM musicas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Musica?

    @Query("SELECT * FROM musicas WHERE isFavorito = 1")
    suspend fun buscarFavoritos(): List<Musica>

    @Query("SELECT * FROM musicas ORDER BY reproducoes DESC")
    suspend fun buscarTopStats(): List<Musica>
}
