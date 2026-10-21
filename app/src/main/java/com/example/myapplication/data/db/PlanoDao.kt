package com.example.myapplication.data.db

import androidx.room.*
import com.example.myapplication.model.Plano

@Dao
interface PlanoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(plano: Plano): Long

    @Query("SELECT * FROM planos")
    suspend fun buscarTodos(): List<Plano>

    @Update
    suspend fun atualizar(plano: Plano): Int

    @Delete
    suspend fun deletar(plano: Plano): Int

    @Query("SELECT * FROM planos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Plano?

    @Query("SELECT * FROM planos WHERE isAtivo = 1 LIMIT 1")
    suspend fun buscarPlanoAtivo(): Plano?
}
