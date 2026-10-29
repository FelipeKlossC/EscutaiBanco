package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository
import com.example.myapplication.navigation.Rotas
import com.example.myapplication.viewmodel.MusicaViewModel
import com.example.myapplication.viewmodel.MusicaViewModelFactory
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PlaylistViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModelFactory(
            PlaylistRepository(AppDatabase.getDatabase(LocalContext.current).playlistDao()),
            MusicaRepository(AppDatabase.getDatabase(LocalContext.current).musicaDao())
        )
    ),
    musicaViewModel: MusicaViewModel = viewModel(
        factory = MusicaViewModelFactory(
            MusicaRepository(AppDatabase.getDatabase(LocalContext.current).musicaDao()),
            PlaylistRepository(AppDatabase.getDatabase(LocalContext.current).playlistDao())
        )
    )
) {
    val playlistUiState by playlistViewModel.uiState.collectAsStateWithLifecycle()
    val musicaUiState by musicaViewModel.uiState.collectAsStateWithLifecycle()
    val playlists = playlistUiState.listaDePlaylists

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0B1D))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate(Rotas.PERFIL) },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color(0xFF0D0B1D)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Logo Equalizador",
                    tint = Color(0xFFD946EF),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Escutaí",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE0AAFF)
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartilhar",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1A3A)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE11D48), Color(0xFF31103F))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Album,
                                contentDescription = "Capa Álbum Starboy",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "STARBOY",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Yellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "STARBOY - The Weeknd",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Faixa",
                            tint = Color(0xFFD946EF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Party Monster",
                            fontSize = 15.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Shuffle, contentDescription = "Aleatório", tint = Color.Gray)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior", tint = Color.White)
                        }
                        FloatingActionButton(
                            onClick = { musicaViewModel.onPlayPause() },
                            shape = CircleShape,
                            containerColor = Color.White,
                            contentColor = Color(0xFF0D0B1D),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = if (musicaUiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Próxima", tint = Color.White)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Repeat, contentDescription = "Repetir", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = musicaUiState.progressoPlayer,
                        onValueChange = { musicaViewModel.onProgressoChange(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFD946EF),
                            activeTrackColor = Color(0xFFD946EF),
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                val primeiraPlaylist = playlists.firstOrNull()
                if (primeiraPlaylist != null) {
                    navController.navigate(Rotas.detalhePlaylist(primeiraPlaylist.id))
                }
            },
            enabled = playlists.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (playlists.isNotEmpty()) "abrir essa playlist" else "nenhuma playlist criada",
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suas Playlists",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(onClick = { navController.navigate(Rotas.LISTA_PLAYLISTS) }) {
                    Text("Ver todas", color = Color(0xFFE0AAFF))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (playlists.isEmpty()) {
                Text(
                    text = "Nenhuma playlist cadastrada ainda.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                playlists.take(2).forEach { playlist ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF8A1B88)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { navController.navigate(Rotas.detalhePlaylist(playlist.id)) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF5B0E5B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Nota Musical",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = playlist.nome,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${playlist.listaDeIds().size} faixas",
                                        fontSize = 13.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                            Text(
                                text = "${playlist.listaDeIds().size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Rotas.LISTA_MUSICAS) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381140)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Gerenciar Músicas",
                    tint = Color(0xFFE0AAFF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerenciar Músicas do App", color = Color.White)
            }
        }
    }
}
