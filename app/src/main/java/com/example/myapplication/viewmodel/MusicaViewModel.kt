package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.Musica
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicaViewModel(
    private val musicaRepository: MusicaRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicaUiState())
    val uiState: StateFlow<MusicaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                musicaRepository.getAllMusicas().collect { musicas ->
                    _uiState.update { it.copy(listaDeMusicas = musicas, isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar musicas", "Msg: ${e.message}")
            }
        }

        viewModelScope.launch {
            try {
                playlistRepository.getAllPlaylists().collect { playlists ->
                    _uiState.update { it.copy(listaDePlaylists = playlists) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar playlists", "Msg: ${e.message}")
            }
        }
    }

    fun onTituloChange(novoTitulo: String) {
        _uiState.update { it.copy(titulo = novoTitulo) }
    }

    fun onArtistaChange(novoArtista: String) {
        _uiState.update { it.copy(artista = novoArtista) }
    }

    fun onDuracaoChange(novaDuracao: String) {
        _uiState.update { it.copy(duracaoTexto = novaDuracao) }
    }

    fun onGeneroChange(novoGenero: String) {
        _uiState.update { it.copy(genero = novoGenero) }
    }

    fun onAdicionar() {
        val state = _uiState.value
        val t = state.titulo.trim()
        val a = state.artista.trim()
        val durSeg = state.duracaoTexto.toIntOrNull()
        val g = if (state.genero.isBlank()) "Pop" else state.genero.trim()

        if (t.isNotBlank() && a.isNotBlank() && durSeg != null) {
            viewModelScope.launch {
                try {
                    musicaRepository.inserir(
                        Musica(
                            titulo = t,
                            artista = a,
                            album = "Single",
                            duracaoSegundos = durSeg,
                            genero = g
                        )
                    )
                    _uiState.update {
                        it.copy(
                            titulo = "",
                            artista = "",
                            duracaoTexto = "",
                            genero = "",
                            erroFormulario = false
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Erro ao inserir", "Msg: ${e.message}")
                }
            }
        } else {
            _uiState.update { it.copy(erroFormulario = true) }
        }
    }

    fun onEditar(musica: Musica) {
        _uiState.update {
            it.copy(
                musicaEmEdicao = musica,
                editTitulo = musica.titulo,
                editArtista = musica.artista,
                editDuracao = musica.duracaoSegundos.toString(),
                editGenero = musica.genero
            )
        }
    }

    fun onEditTituloChange(novoTitulo: String) {
        _uiState.update { it.copy(editTitulo = novoTitulo) }
    }

    fun onEditArtistaChange(novoArtista: String) {
        _uiState.update { it.copy(editArtista = novoArtista) }
    }

    fun onEditDuracaoChange(novaDuracao: String) {
        _uiState.update { it.copy(editDuracao = novaDuracao) }
    }

    fun onEditGeneroChange(novoGenero: String) {
        _uiState.update { it.copy(editGenero = novoGenero) }
    }

    fun onSalvarEdicao() {
        val state = _uiState.value
        val musica = state.musicaEmEdicao ?: return
        val t = state.editTitulo.trim().ifBlank { musica.titulo }
        val a = state.editArtista.trim().ifBlank { musica.artista }
        val durSeg = state.editDuracao.toIntOrNull() ?: musica.duracaoSegundos
        val g = state.editGenero.trim().ifBlank { musica.genero }

        val musicaAtualizada = musica.copy(
            titulo = t,
            artista = a,
            duracaoSegundos = durSeg,
            genero = g
        )

        viewModelScope.launch {
            try {
                musicaRepository.atualizar(musicaAtualizada)
                _uiState.update { it.copy(musicaEmEdicao = null) }
            } catch (e: Exception) {
                Log.e("Erro ao atualizar", "Msg: ${e.message}")
            }
        }
    }

    fun onCancelarEdicao() {
        _uiState.update { it.copy(musicaEmEdicao = null) }
    }

    fun onFavoritar(musica: Musica) {
        viewModelScope.launch {
            try {
                val musicaAtualizada = musica.copy(isFavorito = !musica.isFavorito)
                musicaRepository.atualizar(musicaAtualizada)
                if (_uiState.value.musicaSelecionada?.id == musica.id) {
                    val recarregada = musicaRepository.buscarPorId(musica.id)
                    _uiState.update { it.copy(musicaSelecionada = recarregada) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao alternar favorito", "Msg: ${e.message}")
            }
        }
    }

    fun onDelete(musica: Musica) {
        viewModelScope.launch {
            try {
                musicaRepository.deletar(musica)
                for (pl in _uiState.value.listaDePlaylists) {
                    if (musica.id in pl.listaDeIds()) {
                        val novaLista = pl.listaDeIds().filter { it != musica.id }
                        playlistRepository.atualizar(pl.copy(musicasIds = novaLista.joinToString(",")))
                    }
                }
            } catch (e: Exception) {
                Log.e("Erro ao deletar", "Msg: ${e.message}")
            }
        }
    }

    fun onCarregarDetalhe(id: Int) {
        viewModelScope.launch {
            try {
                val item = musicaRepository.buscarPorId(id)
                _uiState.update {
                    it.copy(
                        musicaSelecionada = item,
                        isLoadingDetalhe = false
                    )
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar por id", "Msg: ${e.message}")
                _uiState.update { it.copy(isLoadingDetalhe = false) }
            }
        }
    }

    fun onPlayPause() {
        val tocandoAgora = !_uiState.value.isPlaying
        _uiState.update { it.copy(isPlaying = tocandoAgora) }
        if (tocandoAgora) {
            val selecionada = _uiState.value.musicaSelecionada
            if (selecionada != null) {
                viewModelScope.launch {
                    try {
                        val musicaAtualizada = selecionada.copy(reproducoes = selecionada.reproducoes + 1)
                        musicaRepository.atualizar(musicaAtualizada)
                        _uiState.update { it.copy(musicaSelecionada = musicaAtualizada) }
                    } catch (e: Exception) {
                        Log.e("Erro ao atualizar reproducoes", "Msg: ${e.message}")
                    }
                }
            }
        }
    }

    fun onProgressoChange(valor: Float) {
        _uiState.update { it.copy(progressoPlayer = valor) }
    }
}
