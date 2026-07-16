package com.jurnal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jurnal.data.Journal
import com.jurnal.data.JournalRepository
import com.jurnal.data.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                    title = { Text("📓 Jurnal Pribadi", fontWeight = FontWeight.Bold) }
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
                    Text("Belum ada jurnal.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                JournalList(journals, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun JournalList(journals: List<Journal>, modifier: Modifier = Modifier) {
    val sorted = remember(journals) {
        journals.sortedByDescending { it.date }
    }

    val groups = remember(sorted) {
        val map = linkedMapOf<String, MutableList<Journal>>()
        for (j in sorted) {
            val day = j.date.take(10)
            map.getOrPut(day) { mutableListOf() }.add(j)
        }
        map
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        groups.forEach { (dateKey, entries) ->
            item(key = "header_$dateKey") {
                DayHeader(dateKey, entries.size)
            }
            items(entries, key = { it.title + it.date }) { journal ->
                JournalCard(journal)
            }
        }
    }
}

@Composable
fun DayHeader(dateKey: String, count: Int) {
    val parts = dateKey.split("-")
    val bulanIndo = listOf("Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des")
    val niceDate = "${parts[2].toInt()} ${bulanIndo[parts[1].toInt() - 1]} ${parts[0]}"

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(niceDate, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text("· $count", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        HorizontalDivider(modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun JournalCard(journal: Journal) {
    var expanded by remember { mutableStateOf(false) }
    val truncated = journal.content.length > 120

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(journal.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)

            // Date + tags
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(journal.date.take(16).replace("T", " "), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Image
            if (journal.image.isNotBlank() && journal.image != "-") {
                Spacer(Modifier.height(10.dp))
                Image(
                    painter = rememberAsyncImagePainter(journal.image),
                    contentDescription = "Gambar",
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Content
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (expanded || !truncated) journal.content else journal.content.take(120) + "...",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Read more
            if (truncated) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(top = 2.dp, start = -12.dp)
                ) {
                    Text(if (expanded) "Ringkas" else "Baca selengkapnya", fontSize = 13.sp)
                }
            }

            // Mood + Music compact card
            if (journal.mood.isNotBlank() && journal.mood != "-" ||
                journal.music.isNotBlank() && journal.music != "-") {
                Spacer(Modifier.height(4.dp))
                MoodMusicCard(journal)
            }
        }
    }
}

@Composable
fun MoodMusicCard(journal: Journal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album cover or fallback
        if (journal.albumArt.isNotBlank() && journal.albumArt != "-") {
            Image(
                painter = rememberAsyncImagePainter(journal.albumArt),
                contentDescription = "Album cover",
                modifier = Modifier.size(36.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else if (journal.music.isNotBlank() && journal.music != "-") {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("♫", fontSize = 16.sp)
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (journal.music.isNotBlank() && journal.music != "-") {
                Text(journal.music, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (journal.mood.isNotBlank() && journal.mood != "-") {
                if (journal.music.isNotBlank() && journal.music != "-") {
                    Spacer(Modifier.height(2.dp))
                }
                Text(journal.mood, fontSize = 15.sp)
            }
        }
    }
}
