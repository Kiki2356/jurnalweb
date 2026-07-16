package com.jurnal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurnal.data.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(tokenManager: TokenManager, onDone: () -> Unit) {
    var token by remember { mutableStateOf(tokenManager.token) }
    var repo by remember { mutableStateOf(tokenManager.repo) }
    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔐 Konfigurasi GitHub", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("Masukkan token dan repo untuk mulai menyimpan jurnal.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Personal Access Token") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = repo,
                onValueChange = { repo = it },
                label = { Text("Repository (username/nama-repo)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Kiki2356/jurnalweb") }
            )

            Spacer(Modifier.height(24.dp))

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (token.isBlank() || repo.isBlank()) {
                        error = "Token dan repo wajib diisi"
                        return@Button
                    }
                    if (!repo.contains("/")) {
                        error = "Format repo harus username/nama-repo"
                        return@Button
                    }
                    tokenManager.token = token
                    tokenManager.repo = repo
                    onDone()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Simpan Konfigurasi")
            }
        }
    }
}
