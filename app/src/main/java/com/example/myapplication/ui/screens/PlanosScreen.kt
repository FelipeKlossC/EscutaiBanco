package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.AppRepository
import com.example.myapplication.model.Plano
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanosScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var planos by remember { mutableStateOf<List<Plano>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensagemSucesso by remember { mutableStateOf(false) }
    var nomePlanoAtivado by remember { mutableStateOf("") }

    fun carregarDados() {
        coroutineScope.launch(Dispatchers.IO) {
            val lista = AppRepository.carregarPlanos()
            planos = lista
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarDados()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planos Mensais e Anuais", color = Color.White) },
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
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Premium",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Escolha o seu Plano VIP",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Ouça suas músicas sem interrupções e com a melhor qualidade de áudio.",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD946EF))
                }
            } else {
                planos.forEach { plano ->
                    val isSelecionado = plano.isAtivo

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelecionado) Color(0xFF8A1B88) else Color(0xFF1E1A3A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(plano.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        if (plano.economia.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFF9800), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(plano.economia, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                        }
                                    }
                                    Text(plano.descricaoCobranca, fontSize = 12.sp, color = Color.LightGray)
                                }
                                Text(plano.preco, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0AAFF))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            plano.beneficios.forEach { beneficio ->
                                Text("• $beneficio", color = Color.White, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        AppRepository.selecionarPlano(plano.id)
                                        carregarDados()
                                    }
                                    nomePlanoAtivado = plano.nome
                                    mensagemSucesso = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelecionado) Color.White else Color(0xFF8A1B88)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isSelecionado) "Plano Ativo" else "Selecionar ${plano.nome}",
                                    color = if (isSelecionado) Color(0xFF8A1B88) else Color.White
                                )
                            }
                        }
                    }
                }
            }

            if (mensagemSucesso) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Parabéns! Você ativou o $nomePlanoAtivado com sucesso no banco Room!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
