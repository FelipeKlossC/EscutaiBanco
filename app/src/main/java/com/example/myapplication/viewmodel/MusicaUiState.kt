package com.example.myapplication.viewmodel

import com.example.myapplication.data.local.Musica
import com.example.myapplication.data.local.Playlist

data class MusicaUiState(
    val listaDeMusicas: List<Musica> = emptyList(),
    val listaDePlaylists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
    val titulo: String = "",
    val artista: String = "",
    val duracaoTexto: String = "",
    val genero: String = "",
    val erroFormulario: Boolean = false,
    val musicaEmEdicao: Musica? = null,
    val editTitulo: String = "",
    val editArtista: String = "",
    val editDuracao: String = "",
    val editGenero: String = "",
    val musicaSelecionada: Musica? = null,
    val isLoadingDetalhe: Boolean = true,
    val isPlaying: Boolean = false,
    val progressoPlayer: Float = 0.35f
) {
    val musicasFavoritas: List<Musica>
        get() = listaDeMusicas.filter { it.isFavorito }
    val musicasMaisOuvidas: List<Musica>
        get() = listaDeMusicas.sortedByDescending { it.reproducoes }
    val totalMusicas: Int
        get() = listaDeMusicas.size
    val totalPlaylists: Int
        get() = listaDePlaylists.size
}
