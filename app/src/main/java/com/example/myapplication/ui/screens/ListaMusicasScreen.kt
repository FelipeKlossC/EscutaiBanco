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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.AppRepository
import com.example.myapplication.model.Musica
import com.example.myapplication.navigation.Rotas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaMusicasScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var listaMusicas by remember { mutableStateOf<List<Musica>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var titulo by remember { mutableStateOf("") }
    var artista by remember { mutableStateOf("") }
    var duracaoTexto by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var erroFormulario by remember { mutableStateOf(false) }

    var musicaParaEditar by remember { mutableStateOf<Musica?>(null) }
    var editTitulo by remember { mutableStateOf("") }
    var editArtista by remember { mutableStateOf("") }
    var editDuracao by remember { mutableStateOf("") }
    var editGenero by remember { mutableStateOf("") }

    fun atualizarLista() {
        coroutineScope.launch(Dispatchers.IO) {
            val dados = AppRepository.carregarMusicas()
            listaMusicas = dados
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        atualizarLista()
    }

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
                        value = titulo,
                        onValueChange = { titulo = it },
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
                            value = artista,
                            onValueChange = { artista = it },
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
                            value = duracaoTexto,
                            onValueChange = { duracaoTexto = it },
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
                        value = genero,
                        onValueChange = { genero = it },
                        label = { Text("Gênero Musical") },
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
                            text = "Preencha todos os campos corretamente!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val t = titulo.trim()
                            val a = artista.trim()
                            val durSeg = duracaoTexto.toIntOrNull()
                            val g = if (genero.isBlank()) "Pop" else genero.trim()

                            if (t.isNotBlank() && a.isNotBlank() && durSeg != null) {
                                titulo = ""
                                artista = ""
                                duracaoTexto = ""
                                genero = ""
                                erroFormulario = false

                                coroutineScope.launch(Dispatchers.IO) {
                                    AppRepository.adicionarMusica(
                                        Musica(
                                            titulo = t,
                                            artista = a,
                                            album = "Single",
                                            duracaoSegundos = durSeg,
                                            genero = g
                                        )
                                    )
                                    atualizarLista()
                                }
                            } else {
                                erroFormulario = true
                            }
                        },
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
                text = "Músicas Cadastradas (${listaMusicas.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD946EF))
                }
            } else if (listaMusicas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma música cadastrada no banco.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaMusicas, key = { it.id }) { musica ->
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
                                    IconButton(onClick = {
                                        musicaParaEditar = musica
                                        editTitulo = musica.titulo
                                        editArtista = musica.artista
                                        editDuracao = musica.duracaoSegundos.toString()
                                        editGenero = musica.genero
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar Música",
                                            tint = Color(0xFFE0AAFF)
                                        )
                                    }

                                    IconButton(onClick = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            AppRepository.toggleFavorito(musica)
                                            atualizarLista()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (musica.isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = if (musica.isFavorito) Color.Red else Color.Gray
                                        )
                                    }

                                    IconButton(onClick = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            AppRepository.deletarMusica(musica)
                                            atualizarLista()
                                        }
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

    musicaParaEditar?.let { musica ->
        AlertDialog(
            onDismissRequest = { musicaParaEditar = null },
            title = { Text("Editar Música", color = Color.White) },
            containerColor = Color(0xFF1E1A3A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitulo,
                        onValueChange = { editTitulo = it },
                        label = { Text("Título") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = editArtista,
                        onValueChange = { editArtista = it },
                        label = { Text("Artista") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = editDuracao,
                        onValueChange = { editDuracao = it },
                        label = { Text("Duração (s)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = editGenero,
                        onValueChange = { editGenero = it },
                        label = { Text("Gênero") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t = editTitulo.trim().ifBlank { musica.titulo }
                        val a = editArtista.trim().ifBlank { musica.artista }
                        val durSeg = editDuracao.toIntOrNull() ?: musica.duracaoSegundos
                        val g = editGenero.trim().ifBlank { musica.genero }

                        val musicaAtualizada = musica.copy(
                            titulo = t,
                            artista = a,
                            duracaoSegundos = durSeg,
                            genero = g
                        )
                        musicaParaEditar = null

                        coroutineScope.launch(Dispatchers.IO) {
                            AppRepository.atualizarMusica(musicaAtualizada)
                            atualizarLista()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1B88))
                ) {
                    Text("Salvar no Banco")
                }
            },
            dismissButton = {
                TextButton(onClick = { musicaParaEditar = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}
