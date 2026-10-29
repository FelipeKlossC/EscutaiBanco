package com.example.myapplication.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicaDao {
    @Insert
    suspend fun inserir(musica: Musica)

    @Query("SELECT * FROM musicas")
    fun buscarTodos(): Flow<List<Musica>>

    @Update
    suspend fun atualizar(musica: Musica)

    @Delete
    suspend fun deletar(musica: Musica)

    @Query("SELECT * FROM musicas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Musica?
}
