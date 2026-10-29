package com.example.myapplication.ui.screens

data class Plano(
    val id: Int = 0,
    val nome: String,
    val tipo: String,
    val preco: String,
    val descricaoCobranca: String,
    val economia: String = "",
    val beneficios: List<String> = emptyList(),
    val isAtivo: Boolean = false
)
