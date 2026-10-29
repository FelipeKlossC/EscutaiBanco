package com.example.myapplication.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "musicas")
data class Musica(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val artista: String,
    val album: String,
    val duracaoSegundos: Int,
    val genero: String,
    val isFavorito: Boolean = false,
    val reproducoes: Int = 0
) {
    fun duracaoFormatada(): String {
        val minutos = duracaoSegundos / 60
        val segundos = duracaoSegundos % 60
        return "%d:%02d".format(minutos, segundos)
    }
}
