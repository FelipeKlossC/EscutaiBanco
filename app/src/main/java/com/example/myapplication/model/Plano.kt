package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planos")
data class Plano(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val tipo: String,
    val preco: String,
    val descricaoCobranca: String,
    val economia: String = "",
    val beneficios: List<String> = emptyList(),
    val isAtivo: Boolean = false
)
