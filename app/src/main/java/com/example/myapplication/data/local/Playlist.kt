package com.example.myapplication.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String,
    val musicasIds: String = ""
) {
    fun listaDeIds(): List<Int> {
        if (musicasIds.isBlank()) return emptyList()
        return musicasIds.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
