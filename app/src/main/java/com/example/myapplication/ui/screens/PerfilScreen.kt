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
import com.example.myapplication.viewmodel.MusicaViewModel
import com.example.myapplication.viewmodel.MusicaViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: MusicaViewModel = viewModel(
        factory = MusicaViewModelFactory(
            MusicaRepository(AppDatabase.getDatabase(LocalContext.current).musicaDao()),
            PlaylistRepository(AppDatabase.getDatabase(LocalContext.current).playlistDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalMusicas = uiState.totalMusicas
    val totalPlaylists = uiState.totalPlaylists

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil", color = Color.White) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF8A1B88), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Usuário Escutaí", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("usuario@escutai.com.br", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381140)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Rotas.PLANOS) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Plano", tint = Color.Yellow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gerenciar Planos VIP", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ver opções de Planos Mensais e Anuais", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Abrir", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate(Rotas.HISTORICO) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1B88)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.History, contentDescription = "Histórico", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Histórico de Músicas Ouvidas", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sua Atividade no App", fontWeight = FontWeight.Bold, color = Color(0xFFE0AAFF), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Músicas Cadastradas no Banco", color = Color.Gray)
                        Text("$totalMusicas", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Playlists Criadas no Banco", color = Color.Gray)
                        Text("$totalPlaylists", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Histórico de Execuções", color = Color.Gray)
                        Text("${historicoMusicas.size} músicas", color = Color(0xFFD946EF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
