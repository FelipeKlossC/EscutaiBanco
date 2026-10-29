package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.MusicaRepository
import com.example.myapplication.data.repository.PlaylistRepository
import com.example.myapplication.navigation.Rotas
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PlaylistViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPlaylistsScreen(
    navController: NavController,
    viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModelFactory(
            PlaylistRepository(AppDatabase.getDatabase(LocalContext.current).playlistDao()),
            MusicaRepository(AppDatabase.getDatabase(LocalContext.current).musicaDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suas Playlists", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1A3A)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFF0D0B1D)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A3A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Criar Nova Playlist",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0AAFF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.nome,
                        onValueChange = { viewModel.onNomeChange(it) },
                        label = { Text("Nome da Playlist") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFD946EF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.descricao,
                        onValueChange = { viewModel.onDescricaoChange(it) },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFD946EF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    if (uiState.erroFormulario) {
                        Text(
                            text = "Insira um nome válido para a playlist!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.onAdicionar() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1B88)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Queue, contentDescription = "Criar")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Criar Playlist no Banco")
                    }
                }
            }

            Text(
                text = "Playlists Disponíveis (${uiState.listaDePlaylists.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD946EF))
                }
            } else if (uiState.listaDePlaylists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma playlist criada no banco.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.listaDePlaylists, key = { it.id }) { playlist ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF8A1B88)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Rotas.detalhePlaylist(playlist.id))
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playlist.nome,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = playlist.descricao,
                                        fontSize = 13.sp,
                                        color = Color.LightGray
                                    )
                                    Text(
                                        text = "${playlist.listaDeIds().size} músicas na lista",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE0AAFF),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.onEditar(playlist) }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar Playlist",
                                            tint = Color.White
                                        )
                                    }

                                    IconButton(onClick = { viewModel.onDelete(playlist) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir Playlist",
                                            tint = Color(0xFFFF5555)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.playlistEmEdicao?.let { _ ->
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarEdicao() },
            title = { Text("Editar Playlist", color = Color.White) },
            containerColor = Color(0xFF1E1A3A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.editNome,
                        onValueChange = { viewModel.onEditNomeChange(it) },
                        label = { Text("Nome da Playlist") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = uiState.editDescricao,
                        onValueChange = { viewModel.onEditDescricaoChange(it) },
                        label = { Text("Descrição") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onSalvarEdicao() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1B88))
                ) {
                    Text("Salvar no Banco")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarEdicao() }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}
