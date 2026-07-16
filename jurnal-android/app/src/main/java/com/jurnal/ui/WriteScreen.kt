package com.jurnal.ui

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jurnal.data.Journal
import com.jurnal.data.JournalRepository
import com.jurnal.data.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

val moods = listOf("😊", "😌", "🥰", "😢", "😡", "😴", "🤔", "🔥")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(tokenManager: TokenManager, prefill: String = "", onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf(prefill) }
    var mood by remember { mutableStateOf("") }
    var music by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Jurnal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Kembali") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi Jurnal") },
                modifier = Modifier.fillMaxWidth().height(250.dp),
                maxLines = 20
            )

            Spacer(Modifier.height(8.dp))

            // Mic button
            val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if (data != null) {
                    val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!results.isNullOrEmpty()) {
                        content = if (content.isBlank()) results[0] else "$content ${results[0]}"
                    }
                }
            }
            Button(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Bicara untuk menulis jurnal...")
                    }
                    try {
                        voiceLauncher.launch(intent)
                    } catch (e: Exception) {
                        message = "Voice tidak tersedia di perangkat ini"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("🎤 Voice to Text")
            }

            Spacer(Modifier.height(16.dp))

            // Image picker
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var imageBase64 by remember { mutableStateOf<String?>(null) }

            val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    imageUri = it
                    // Read as base64 for upload
                    try {
                        val stream = context.contentResolver.openInputStream(it)
                        val bytes = stream?.readBytes() ?: return@let
                        stream.close()
                        imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes)
                    } catch (e: Exception) {
                        message = "Gagal baca gambar"
                    }
                }
            }

            Text("Gambar", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            if (imageUri != null) {
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd).padding(4.dp)
                            .size(28.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                            .clickable { imageUri = null; imageBase64 = null },
                        contentAlignment = Alignment.Center
                    ) { Text("✕", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) }
                }
            } else {
                OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Tambah gambar")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Mood", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { m ->
                    FilterChip(
                        selected = mood == m,
                        onClick = { mood = if (mood == m) "" else m },
                        label = { Text(m, fontSize = 18.sp) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = music,
                onValueChange = { music = it },
                label = { Text("Musik (opsional — Judul - Artis)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (content.isBlank()) {
                        message = "Isi jurnal tidak boleh kosong"
                        return@Button
                    }
                    saving = true
                    message = "Menyimpan..."
                    scope.launch {
                        val repo = JournalRepository(tokenManager.token, tokenManager.repo)
                        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date())
                        val journal = Journal(
                            title = title.ifBlank { content.split(" ").take(5).joinToString(" ") },
                            date = now,
                            mood = mood.ifEmpty { "-" },
                            music = music.ifBlank { "-" },
                            content = content
                        )
                        val ok = repo.saveJournal(journal, imageBase64)
                        if (ok) {
                            message = "✅ Jurnal tersimpan!"
                            title = ""; content = ""; mood = ""; music = ""
                            imageUri = null; imageBase64 = null
                        } else {
                            message = "❌ Gagal menyimpan. Cek token GitHub."
                        }
                        saving = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !saving
            ) {
                Text(if (saving) "Menyimpan..." else "💾 Simpan Jurnal")
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
