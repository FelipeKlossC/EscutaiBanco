package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.Playlist
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val musicaRepository: MusicaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                playlistRepository.getAllPlaylists().collect { playlists ->
                    _uiState.update { it.copy(listaDePlaylists = playlists, isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar playlists", "Msg: ${e.message}")
            }
        }

        viewModelScope.launch {
            try {
                musicaRepository.getAllMusicas().collect { musicas ->
                    _uiState.update { it.copy(todasMusicas = musicas) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar musicas", "Msg: ${e.message}")
            }
        }
    }

    fun onNomeChange(novoNome: String) {
        _uiState.update { it.copy(nome = novoNome) }
    }

    fun onDescricaoChange(novaDescricao: String) {
        _uiState.update { it.copy(descricao = novaDescricao) }
    }

    fun onAdicionar() {
        val state = _uiState.value
        val n = state.nome.trim()
        val desc = if (state.descricao.isBlank()) "Playlist personalizada" else state.descricao.trim()

        if (n.isNotBlank()) {
            viewModelScope.launch {
                try {
                    playlistRepository.inserir(Playlist(nome = n, descricao = desc))
                    _uiState.update {
                        it.copy(
                            nome = "",
                            descricao = "",
                            erroFormulario = false
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Erro ao inserir playlist", "Msg: ${e.message}")
                }
            }
        } else {
            _uiState.update { it.copy(erroFormulario = true) }
        }
    }

    fun onEditar(playlist: Playlist) {
        _uiState.update {
            it.copy(
                playlistEmEdicao = playlist,
                editNome = playlist.nome,
                editDescricao = playlist.descricao
            )
        }
    }

    fun onEditNomeChange(novoNome: String) {
        _uiState.update { it.copy(editNome = novoNome) }
    }

    fun onEditDescricaoChange(novaDescricao: String) {
        _uiState.update { it.copy(editDescricao = novaDescricao) }
    }

    fun onSalvarEdicao() {
        val state = _uiState.value
        val playlist = state.playlistEmEdicao ?: return
        val n = state.editNome.trim().ifBlank { playlist.nome }
        val desc = state.editDescricao.trim().ifBlank { playlist.descricao }

        val playlistAtualizada = playlist.copy(nome = n, descricao = desc)

        viewModelScope.launch {
            try {
                playlistRepository.atualizar(playlistAtualizada)
                _uiState.update { it.copy(playlistEmEdicao = null) }
            } catch (e: Exception) {
                Log.e("Erro ao atualizar playlist", "Msg: ${e.message}")
            }
        }
    }

    fun onCancelarEdicao() {
        _uiState.update { it.copy(playlistEmEdicao = null) }
    }

    fun onDelete(playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistRepository.deletar(playlist)
            } catch (e: Exception) {
                Log.e("Erro ao deletar playlist", "Msg: ${e.message}")
            }
        }
    }

    fun onCarregarDetalhe(id: Int) {
        viewModelScope.launch {
            try {
                val pl = playlistRepository.buscarPorId(id)
                _uiState.update {
                    it.copy(
                        playlistSelecionada = pl,
                        isLoadingDetalhe = false
                    )
                }
            } catch (e: Exception) {
                Log.e("Erro ao buscar playlist por id", "Msg: ${e.message}")
                _uiState.update { it.copy(isLoadingDetalhe = false) }
            }
        }
    }

    fun onMostrarDialogAdicionar() {
        _uiState.update { it.copy(mostrarDialogAdicionar = true) }
    }

    fun onFecharDialogAdicionar() {
        _uiState.update { it.copy(mostrarDialogAdicionar = false) }
    }

    fun onAdicionarMusica(musicaId: Int) {
        val playlistId = _uiState.value.playlistSelecionada?.id ?: return
        viewModelScope.launch {
            try {
                val pl = playlistRepository.buscarPorId(playlistId)
                if (pl != null && musicaId !in pl.listaDeIds()) {
                    val novaLista = pl.listaDeIds() + musicaId
                    playlistRepository.atualizar(pl.copy(musicasIds = novaLista.joinToString(",")))
                    val atualizada = playlistRepository.buscarPorId(playlistId)
                    _uiState.update {
                        it.copy(
                            playlistSelecionada = atualizada,
                            mostrarDialogAdicionar = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Erro ao adicionar musica na playlist", "Msg: ${e.message}")
            }
        }
    }

    fun onRemoverMusica(musicaId: Int) {
        val playlistId = _uiState.value.playlistSelecionada?.id ?: return
        viewModelScope.launch {
            try {
                val pl = playlistRepository.buscarPorId(playlistId)
                if (pl != null && musicaId in pl.listaDeIds()) {
                    val novaLista = pl.listaDeIds().filter { it != musicaId }
                    playlistRepository.atualizar(pl.copy(musicasIds = novaLista.joinToString(",")))
                    val atualizada = playlistRepository.buscarPorId(playlistId)
                    _uiState.update { it.copy(playlistSelecionada = atualizada) }
                }
            } catch (e: Exception) {
                Log.e("Erro ao remover musica da playlist", "Msg: ${e.message}")
            }
        }
    }
}
