package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.*

sealed class BottomNavItem(val rota: String, val titulo: String, val icone: ImageVector) {
    object Home : BottomNavItem(Rotas.HOME, "Início", Icons.Default.Home)
    object Musicas : BottomNavItem(Rotas.LISTA_MUSICAS, "Músicas", Icons.Default.MusicNote)
    object Playlists : BottomNavItem(Rotas.LISTA_PLAYLISTS, "Playlists", Icons.Default.QueueMusic)
    object Favoritos : BottomNavItem(Rotas.FAVORITOS, "Favoritos", Icons.Default.Favorite)
    object TopStats : BottomNavItem(Rotas.TOP_STATS, "Top", Icons.Default.Whatshot)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Musicas,
        BottomNavItem.Playlists,
        BottomNavItem.Favoritos,
        BottomNavItem.TopStats
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1A3A),
                contentColor = Color.White
            ) {
                bottomItems.forEach { item ->
                    val isSelected = currentRoute == item.rota
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.rota) {
                                navController.navigate(item.rota) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(item.icone, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFFE0AAFF),
                            indicatorColor = Color(0xFF8A1B88),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.HOME) {
                HomeScreen(navController = navController)
            }

            composable(Rotas.LISTA_MUSICAS) {
                ListaMusicasScreen(navController = navController)
            }

            composable(
                route = "${Rotas.DETALHE_MUSICA}/{musicaId}",
                arguments = listOf(navArgument("musicaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val musicaId = backStackEntry.arguments?.getInt("musicaId") ?: 0
                DetalheMusicaScreen(navController = navController, musicaId = musicaId)
            }

            composable(Rotas.LISTA_PLAYLISTS) {
                ListaPlaylistsScreen(navController = navController)
            }

            composable(
                route = "${Rotas.DETALHE_PLAYLIST}/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: 0
                DetalhePlaylistScreen(navController = navController, playlistId = playlistId)
            }

            composable(Rotas.FAVORITOS) {
                FavoritosScreen(navController = navController)
            }

            composable(Rotas.TOP_STATS) {
                TopStatsScreen(navController = navController)
            }

            composable(Rotas.PERFIL) {
                PerfilScreen(navController = navController)
            }

            composable(Rotas.PLANOS) {
                PlanosScreen(navController = navController)
            }

            composable(Rotas.HISTORICO) {
                HistoricoScreen(navController = navController)
            }
        }
    }
}
