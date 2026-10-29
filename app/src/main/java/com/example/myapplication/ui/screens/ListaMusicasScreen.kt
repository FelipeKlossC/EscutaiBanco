package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaMusicasScreen(
    navController: NavController,
    viewModel: MusicaViewModel = viewModel(
        factory = MusicaViewModelFactory(
            MusicaRepository(AppDatabase.getDatabase(LocalContext.current).musicaDao()),
            PlaylistRepository(AppDatabase.getDatabase(LocalContext.current).playlistDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca de Músicas", color = Color.White) },
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
                        text = "Adicionar Nova Música",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0AAFF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.titulo,
                        onValueChange = { viewModel.onTituloChange(it) },
                        label = { Text("Título da Música") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFD946EF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.artista,
                            onValueChange = { viewModel.onArtistaChange(it) },
                            label = { Text("Artista") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFD946EF),
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        OutlinedTextField(
                            value = uiState.duracaoTexto,
                            onValueChange = { viewModel.onDuracaoChange(it) },
                            label = { Text("Duração (s)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFD946EF),
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.genero,
                        onValueChange = { viewModel.onGeneroChange(it) },
                        label = { Text("Gênero Musical") },
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
                            text = "Preencha todos os campos corretamente!",
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
                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar Música no Banco")
                    }
                }
            }

            Text(
                text = "Músicas Cadastradas (${uiState.listaDeMusicas.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD946EF))
                }
            } else if (uiState.listaDeMusicas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma música cadastrada no banco.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.listaDeMusicas, key = { it.id }) { musica ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A3A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Rotas.detalheMusica(musica.id))
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Música",
                                        tint = Color(0xFFD946EF),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = musica.titulo,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${musica.artista} • ${musica.duracaoFormatada()} • ${musica.genero}",
                                            fontSize = 13.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.onEditar(musica) }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar Música",
                                            tint = Color(0xFFE0AAFF)
                                        )
                                    }

                                    IconButton(onClick = { viewModel.onFavoritar(musica) }) {
                                        Icon(
                                            imageVector = if (musica.isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = if (musica.isFavorito) Color.Red else Color.Gray
                                        )
                                    }

                                    IconButton(onClick = {
                                        viewModel.onDelete(musica)
                                        historicoMusicas.removeAll { it.id == musica.id }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover Música",
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

    uiState.musicaEmEdicao?.let { _ ->
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarEdicao() },
            title = { Text("Editar Música", color = Color.White) },
            containerColor = Color(0xFF1E1A3A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.editTitulo,
                        onValueChange = { viewModel.onEditTituloChange(it) },
                        label = { Text("Título") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = uiState.editArtista,
                        onValueChange = { viewModel.onEditArtistaChange(it) },
                        label = { Text("Artista") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = uiState.editDuracao,
                        onValueChange = { viewModel.onEditDuracaoChange(it) },
                        label = { Text("Duração (s)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = uiState.editGenero,
                        onValueChange = { viewModel.onEditGeneroChange(it) },
                        label = { Text("Gênero") },
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
