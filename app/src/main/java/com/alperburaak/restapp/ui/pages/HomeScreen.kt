package com.alperburaak.restapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alperburaak.restapp.ui.auth.AuthViewModel
import com.alperburaak.restapp.data.remote.model.authModell.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val uiState by authViewModel.authState.collectAsState()

    // SADECE ve SADECE "Unauthenticated" ise login'e at.
    // Loading durumunda atma!
    LaunchedEffect(uiState) {
        if (uiState is AuthState.Unauthenticated) {
            onLogout()
        }
    }

    // İçerik Yönetimi (Loading Kontrolü)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RestApp Panel") },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Çıkış Yap",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        // DURUM KONTROLÜ (WHEN YAPISI)
        when (uiState) {
            // DURUM 1: YÜKLENİYOR
            is AuthState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator() // Beyaz ekran yerine bu dönecek
                }
            }

            // DURUM 2: GİRİŞ BAŞARILI
            is AuthState.Authenticated -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ... Senin mevcut HomeScreen içeriğin buraya gelecek ...
                    item { WelcomeCard(userName = "Restoran Sahibi") }

                    item {
                        Text(
                            "Hızlı İşlemler",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DashboardCard(Modifier.weight(1f), "Aktif Siparişler", Icons.Default.Fastfood, "3")
                            DashboardCard(Modifier.weight(1f), "Geçmiş", Icons.Default.History, "120")
                        }
                    }

                    item {
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Restoran Durumu", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Text("Açık • Kapanış 22:00", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // DURUM 3: GİRİŞ YOK (Zaten LaunchedEffect atacak ama boş blok gerekli)
            else -> { }
        }
    }
}

@Composable
fun WelcomeCard(userName: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Hoş Geldin,", style = MaterialTheme.typography.labelLarge)
                Text(userName, style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, count: String) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}