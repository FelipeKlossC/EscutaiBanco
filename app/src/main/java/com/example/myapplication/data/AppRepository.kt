package com.example.myapplication.data

import android.content.Context
import android.util.Log
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.data.db.MusicaDao
import com.example.myapplication.data.db.PlanoDao
import com.example.myapplication.data.db.PlaylistDao
import com.example.myapplication.model.Musica
import com.example.myapplication.model.Plano
import com.example.myapplication.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppRepository {
    private const val TAG = "AppRepository"
    private lateinit var database: AppDatabase
    private lateinit var musicaDao: MusicaDao
    private lateinit var playlistDao: PlaylistDao
    private lateinit var planoDao: PlanoDao

    val historicoMusicas = mutableListOf<Musica>()

    fun init(context: Context) {
        if (!::database.isInitialized) {
            database = AppDatabase.getDatabase(context)
            musicaDao = database.musicaDao()
            playlistDao = database.playlistDao()
            planoDao = database.planoDao()
        }
    }

    suspend fun carregarMusicas(): List<Musica> = withContext(Dispatchers.IO) {
        try {
            val lista = musicaDao.buscarTodos()
            if (lista.isEmpty()) {
                preencherDadosIniciais()
                musicaDao.buscarTodos()
            } else {
                val invalidos = lista.filter { it.titulo.isBlank() || it.artista.isBlank() }
                if (invalidos.isNotEmpty()) {
                    invalidos.forEach { musicaDao.deletar(it) }
                    musicaDao.buscarTodos()
                } else {
                    lista
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar músicas do banco", e)
            emptyList()
        }
    }

    suspend fun carregarPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        try {
            val lista = playlistDao.buscarTodos()
            if (lista.isEmpty()) {
                preencherDadosIniciais()
                playlistDao.buscarTodos()
            } else {
                val invalidas = lista.filter { it.nome.isBlank() }
                if (invalidas.isNotEmpty()) {
                    invalidas.forEach { playlistDao.deletar(it) }
                    playlistDao.buscarTodos()
                } else {
                    lista
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar playlists do banco", e)
            emptyList()
        }
    }

    suspend fun carregarPlanos(): List<Plano> = withContext(Dispatchers.IO) {
        try {
            val lista = planoDao.buscarTodos()
            if (lista.isEmpty()) {
                preencherPlanosIniciais()
                planoDao.buscarTodos()
            } else {
                lista
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar planos do banco", e)
            emptyList()
        }
    }

    private suspend fun preencherDadosIniciais() = withContext(Dispatchers.IO) {
        try {
            val m1 = Musica(titulo = "Starboy", artista = "The Weeknd", album = "Starboy", duracaoSegundos = 230, genero = "R&B / Pop", isFavorito = true, reproducoes = 8420)
            val m2 = Musica(titulo = "Party Monster", artista = "The Weeknd", album = "Starboy", duracaoSegundos = 249, genero = "R&B", isFavorito = true, reproducoes = 5120)
            val m3 = Musica(titulo = "Billie Jean", artista = "Michael Jackson", album = "Thriller", duracaoSegundos = 294, genero = "Pop", isFavorito = false, reproducoes = 12400)
            val m4 = Musica(titulo = "Rebel Yell", artista = "Billy Idol", album = "Rebel Yell", duracaoSegundos = 285, genero = "Rock", isFavorito = true, reproducoes = 3210)
            val m5 = Musica(titulo = "Blinding Lights", artista = "The Weeknd", album = "After Hours", duracaoSegundos = 200, genero = "Synthpop", isFavorito = false, reproducoes = 9800)

            val id1 = musicaDao.inserir(m1).toInt()
            val id2 = musicaDao.inserir(m2).toInt()
            val id3 = musicaDao.inserir(m3).toInt()
            val id4 = musicaDao.inserir(m4).toInt()
            val id5 = musicaDao.inserir(m5).toInt()

            playlistDao.inserir(Playlist(nome = "Playlist #861", descricao = "Minhas preferidas para treinar e correr", musicasIds = listOf(id1, id2, id3)))
            playlistDao.inserir(Playlist(nome = "Playlist #867", descricao = "Sessão Pop Nostalgia Anos 80 e 90", musicasIds = listOf(id3, id4)))
            playlistDao.inserir(Playlist(nome = "Noche de R&B", descricao = "Músicas para relaxar no fim de semana", musicasIds = listOf(id1, id2, id5)))

            preencherPlanosIniciais()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao preencher dados iniciais", e)
        }
    }

    private suspend fun preencherPlanosIniciais() = withContext(Dispatchers.IO) {
        try {
            planoDao.inserir(
                Plano(
                    nome = "Plano Mensal",
                    tipo = "Mensal",
                    preco = "R$ 19,90/mês",
                    descricaoCobranca = "Cobrança mensal recorrente",
                    beneficios = listOf("Áudio Hi-Fi de alta fidelidade", "Sem anúncios comerciais", "Pule quantas faixas quiser"),
                    isAtivo = false
                )
            )
            planoDao.inserir(
                Plano(
                    nome = "Plano Anual VIP",
                    tipo = "Anual",
                    preco = "R$ 179,90/ano",
                    descricaoCobranca = "Cobrado anualmente (R$ 14,99/mês)",
                    economia = "ECONOMIZE 25%",
                    beneficios = listOf("Todos os benefícios do Plano Mensal", "Downloads de playlists ilimitados offline", "Acesso prioritário a lançamentos e shows"),
                    isAtivo = true
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao preencher planos iniciais", e)
        }
    }

    suspend fun selecionarPlano(planoId: Int) = withContext(Dispatchers.IO) {
        try {
            val todos = planoDao.buscarTodos()
            todos.forEach { p ->
                planoDao.atualizar(p.copy(isAtivo = (p.id == planoId)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao selecionar plano", e)
        }
    }

    suspend fun buscarMusicaPorId(id: Int): Musica? = withContext(Dispatchers.IO) {
        try {
            musicaDao.buscarPorId(id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar música por ID: $id", e)
            null
        }
    }

    suspend fun adicionarMusica(musica: Musica): Long = withContext(Dispatchers.IO) {
        try {
            musicaDao.inserir(musica)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inserir música", e)
            -1L
        }
    }

    suspend fun atualizarMusica(musica: Musica) = withContext(Dispatchers.IO) {
        try {
            musicaDao.atualizar(musica)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar música", e)
        }
    }

    suspend fun deletarMusica(musica: Musica) = withContext(Dispatchers.IO) {
        try {
            musicaDao.deletar(musica)
            historicoMusicas.removeAll { it.id == musica.id }
            val playlists = playlistDao.buscarTodos()
            playlists.forEach { p ->
                if (p.musicasIds.contains(musica.id)) {
                    val novaLista = p.musicasIds.filter { it != musica.id }
                    playlistDao.atualizar(p.copy(musicasIds = novaLista))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar música", e)
        }
    }

    suspend fun toggleFavorito(musica: Musica) = withContext(Dispatchers.IO) {
        try {
            val novaMusica = musica.copy(isFavorito = !musica.isFavorito)
            musicaDao.atualizar(novaMusica)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao alterar favorito", e)
        }
    }

    suspend fun buscarPlaylistPorId(id: Int): Playlist? = withContext(Dispatchers.IO) {
        try {
            playlistDao.buscarPorId(id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar playlist por ID: $id", e)
            null
        }
    }

    suspend fun adicionarPlaylist(playlist: Playlist): Long = withContext(Dispatchers.IO) {
        try {
            playlistDao.inserir(playlist)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inserir playlist", e)
            -1L
        }
    }

    suspend fun atualizarPlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        try {
            playlistDao.atualizar(playlist)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar playlist", e)
        }
    }

    suspend fun deletarPlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        try {
            playlistDao.deletar(playlist)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar playlist", e)
        }
    }

    suspend fun adicionarMusicaAPlaylist(playlistId: Int, musicaId: Int) = withContext(Dispatchers.IO) {
        try {
            val playlist = playlistDao.buscarPorId(playlistId)
            if (playlist != null && !playlist.musicasIds.contains(musicaId)) {
                val novaLista = playlist.musicasIds + musicaId
                playlistDao.atualizar(playlist.copy(musicasIds = novaLista))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar música à playlist", e)
        }
    }

    suspend fun removerMusicaDaPlaylist(playlistId: Int, musicaId: Int) = withContext(Dispatchers.IO) {
        try {
            val playlist = playlistDao.buscarPorId(playlistId)
            if (playlist != null) {
                val novaLista = playlist.musicasIds.filter { it != musicaId }
                playlistDao.atualizar(playlist.copy(musicasIds = novaLista))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao remover música da playlist", e)
        }
    }

    suspend fun obterMusicasDaPlaylist(playlistId: Int): List<Musica> = withContext(Dispatchers.IO) {
        try {
            val playlist = playlistDao.buscarPorId(playlistId) ?: return@withContext emptyList()
            val todas = musicaDao.buscarTodos()
            todas.filter { it.id in playlist.musicasIds }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter músicas da playlist", e)
            emptyList()
        }
    }

    suspend fun buscarFavoritos(): List<Musica> = withContext(Dispatchers.IO) {
        try {
            musicaDao.buscarFavoritos()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar favoritos", e)
            emptyList()
        }
    }

    suspend fun buscarTopStats(): List<Musica> = withContext(Dispatchers.IO) {
        try {
            musicaDao.buscarTopStats()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar top stats", e)
            emptyList()
        }
    }

    fun registrarNoHistorico(musica: Musica) {
        if (historicoMusicas.firstOrNull()?.id != musica.id) {
            historicoMusicas.add(0, musica)
        }
    }
}
