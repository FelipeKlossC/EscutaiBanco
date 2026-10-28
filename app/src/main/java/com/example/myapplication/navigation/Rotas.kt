package com.example.myapplication.navigation

object Rotas {
    const val HOME = "home"
    const val LISTA_MUSICAS = "lista_musicas"
    const val DETALHE_MUSICA = "detalhe_musica"
    const val LISTA_PLAYLISTS = "lista_playlists"
    const val DETALHE_PLAYLIST = "detalhe_playlist"
    const val FAVORITOS = "favoritos"
    const val TOP_STATS = "top_stats"
    const val PERFIL = "perfil"
    const val PLANOS = "planos"
    const val HISTORICO = "historico"

    fun detalheMusica(musicaId: Int) = "$DETALHE_MUSICA/$musicaId"
    fun detalhePlaylist(playlistId: Int) = "$DETALHE_PLAYLIST/$playlistId"
}
