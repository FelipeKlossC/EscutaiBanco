package com.example.myapplication.ui.screens

import android.util.Log
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
import androidx.navigation.NavController
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.data.db.PlaylistDao
import com.example.myapplication.model.Playlist
import com.example.myapplication.navigation.Rotas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// funções soltas (top-level), suspend, com try/catch e Log.e
suspend fun buscarPlaylists(playlistDao: PlaylistDao): List<Playlist> {
    return try {
        playlistDao.buscarTodos()
    } catch (e: Exception) {
        Log.e("Erro ao buscar", "Msg: ${e.message}")
        emptyList()
    }
}

suspend fun inserirPlaylist(playlist: Playlist, playlistDao: PlaylistDao) {
    try {
        playlistDao.inserir(playlist)
    } catch (e: Exception) {
        Log.e("Erro ao inserir", "Msg: ${e.message}")
    }
}

suspend fun atualizarPlaylist(playlist: Playlist, playlistDao: PlaylistDao) {
    try {
        playlistDao.atualizar(playlist)
    } catch (e: Exception) {
        Log.e("Erro ao atualizar", "Msg: ${e.message}")
    }
}

suspend fun deletarPlaylist(playlist: Playlist, playlistDao: PlaylistDao) {
    try {
        playlistDao.deletar(playlist)
    } catch (e: Exception) {
        Log.e("Erro ao deletar", "Msg: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPlaylistsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val playlistDao = db.playlistDao()

    var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erroFormulario by remember { mutableStateOf(false) }

    var playlistParaEditar by remember { mutableStateOf<Playlist?>(null) }
    var editNome by remember { mutableStateOf("") }
    var editDescricao by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            playlists = buscarPlaylists(playlistDao)
            isLoading = false
        }
    }

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
                        value = nome,
                        onValueChange = { nome = it },
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
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFD946EF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    if (erroFormulario) {
                        Text(
                            text = "Insira um nome válido para a playlist!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val n = nome.trim()
                            val desc = if (descricao.isBlank()) "Playlist personalizada" else descricao.trim()

                            if (n.isNotBlank()) {
                                nome = ""
                                descricao = ""
                                erroFormulario = false

                                CoroutineScope(Dispatchers.IO).launch {
                                    inserirPlaylist(
                                        Playlist(
                                            nome = n,
                                            descricao = desc
                                        ),
                                        playlistDao
                                    )
                                    playlists = buscarPlaylists(playlistDao)
                                }
                            } else {
                                erroFormulario = true
                            }
                        },
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
                text = "Playlists Disponíveis (${playlists.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD946EF))
                }
            } else if (playlists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma playlist criada no banco.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlists, key = { it.id }) { playlist ->
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
                                    IconButton(onClick = {
                                        playlistParaEditar = playlist
                                        editNome = playlist.nome
                                        editDescricao = playlist.descricao
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar Playlist",
                                            tint = Color.White
                                        )
                                    }

                                    IconButton(onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            deletarPlaylist(playlist, playlistDao)
                                            playlists = buscarPlaylists(playlistDao)
                                        }
                                    }) {
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

    playlistParaEditar?.let { playlist ->
        AlertDialog(
            onDismissRequest = { playlistParaEditar = null },
            title = { Text("Editar Playlist", color = Color.White) },
            containerColor = Color(0xFF1E1A3A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editNome,
                        onValueChange = { editNome = it },
                        label = { Text("Nome da Playlist") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = editDescricao,
                        onValueChange = { editDescricao = it },
                        label = { Text("Descrição") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val n = editNome.trim().ifBlank { playlist.nome }
                        val desc = editDescricao.trim().ifBlank { playlist.descricao }

                        val playlistAtualizada = playlist.copy(
                            nome = n,
                            descricao = desc
                        )
                        playlistParaEditar = null

                        CoroutineScope(Dispatchers.IO).launch {
                            atualizarPlaylist(playlistAtualizada, playlistDao)
                            playlists = buscarPlaylists(playlistDao)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1B88))
                ) {
                    Text("Salvar no Banco")
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistParaEditar = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}
