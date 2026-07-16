package com.jurnal.ui

import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurnal.data.Journal
import com.jurnal.data.JournalRepository
import com.jurnal.data.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(tokenManager: TokenManager, sharedText: String = "") {
    val scope = rememberCoroutineScope()
    var journals by remember { mutableStateOf<List<Journal>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showWrite by remember { mutableStateOf(sharedText.isNotEmpty()) }
    var prefillText by remember { mutableStateOf(sharedText) }

    LaunchedEffect(Unit) {
        val repo = JournalRepository(tokenManager.token, tokenManager.repo)
        journals = repo.fetchJournals()
        loading = false
    }

    if (showWrite) {
        WriteScreen(tokenManager, prefillText) {
            showWrite = false
            prefillText = ""
            scope.launch {
                val repo = JournalRepository(tokenManager.token, tokenManager.repo)
                journals = repo.fetchJournals()
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("📓 Jurnal Pribadi", fontWeight = FontWeight.Bold) },
                    actions = {
                        TextButton(onClick = { showWrite = true }) {
                            Text("✏️ Tulis")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = { showWrite = true }) {
                    Text("✏️ Tulis Baru")
                }
            }
        ) { padding ->
            if (loading) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (journals.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Belum ada jurnal. Tap ✏️ untuk mulai.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                    items(journals) { journal ->
                        JournalCard(journal)
                    }
                }
            }
        }
    }
}

@Composable
fun JournalCard(journal: Journal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(journal.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Row {
                Text(journal.date.take(10), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (journal.tags.isNotEmpty()) {
                    Text(" · ${journal.tags.joinToString(" ")}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(journal.content.take(120) + if (journal.content.length > 120) "..." else "",
                fontSize = 14.sp, maxLines = 3)
            if (journal.mood.isNotBlank() || journal.music.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (journal.mood.isNotBlank()) Text(journal.mood, fontSize = 16.sp)
                    if (journal.music.isNotBlank()) {
                        if (journal.mood.isNotBlank()) Spacer(Modifier.width(8.dp))
                        Text("♫ ${journal.music}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
