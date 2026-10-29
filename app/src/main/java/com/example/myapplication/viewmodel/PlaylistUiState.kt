package com.example.myapplication.viewmodel

import com.example.myapplication.data.local.Musica
import com.example.myapplication.data.local.Playlist

data class PlaylistUiState(
    val listaDePlaylists: List<Playlist> = emptyList(),
    val todasMusicas: List<Musica> = emptyList(),
    val isLoading: Boolean = true,
    val nome: String = "",
    val descricao: String = "",
    val erroFormulario: Boolean = false,
    val playlistEmEdicao: Playlist? = null,
    val editNome: String = "",
    val editDescricao: String = "",
    val playlistSelecionada: Playlist? = null,
    val isLoadingDetalhe: Boolean = true,
    val mostrarDialogAdicionar: Boolean = false
) {
    val musicasDaPlaylist: List<Musica>
        get() = todasMusicas.filter { it.id in (playlistSelecionada?.listaDeIds() ?: emptyList()) }
    val musicasForaDaPlaylist: List<Musica>
        get() = todasMusicas.filter { it.id !in (playlistSelecionada?.listaDeIds() ?: emptyList()) }
    val tempoTotalSegundos: Int
        get() = musicasDaPlaylist.sumOf { it.duracaoSegundos }
    val musicaMaisOuvida: Musica?
        get() = musicasDaPlaylist.maxByOrNull { it.reproducoes }
}
