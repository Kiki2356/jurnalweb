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
import androidx.compose.ui.text.style.TextOverflow
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
            var cameraUri by remember { mutableStateOf<Uri?>(null) }

            val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    imageUri = it
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

            val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    cameraUri?.let { uri ->
                        imageUri = uri
                        try {
                            val stream = context.contentResolver.openInputStream(uri)
                            val bytes = stream?.readBytes() ?: return@let
                            stream.close()
                            imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes)
                        } catch (e: Exception) {
                            message = "Gagal baca foto"
                        }
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) { Text("🖼 Galeri") }
                    OutlinedButton(
                        onClick = {
                            val file = java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("📷 Kamera") }
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

            // Music search
            var musicSearch by remember { mutableStateOf("") }
            var musicResults by remember { mutableStateOf<List<TrackResult>>(emptyList()) }
            var musicLoading by remember { mutableStateOf(false) }
            var musicSelected by remember { mutableStateOf(false) }
            var albumArtUrl by remember { mutableStateOf("") }

            Text("Musik", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            if (musicSelected) {
                // Show selected music with cover
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { musicSelected = false; musicSearch = "" }
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (albumArtUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(albumArtUrl),
                            contentDescription = "Cover",
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center) { Text("♫", fontSize = 18.sp) }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(music, modifier = Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("✕", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                OutlinedTextField(
                    value = musicSearch,
                    onValueChange = { q ->
                        musicSearch = q
                        if (q.length >= 2) {
                            musicLoading = true
                            scope.launch {
                                val results = withContext(Dispatchers.IO) {
                                    try {
                                        val url = java.net.URL("https://jurnalweb.netlify.app/.netlify/functions/deezer?q=" + java.net.URLEncoder.encode(q, "utf-8"))
                                        val json = url.readText()
                                        val resp = com.google.gson.Gson().fromJson(json, MusicResponse::class.java)
                                        resp.data.take(6)
                                    } catch (e: Exception) { emptyList() }
                                }
                                musicResults = results
                                musicLoading = false
                            }
                        } else { musicResults = emptyList() }
                    },
                    label = { Text("Cari lagu atau artis...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Search results dropdown
                if (musicResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {
                            musicResults.forEach { track ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        music = "${track.artist.name} - ${track.title}"
                                        albumArtUrl = track.album.cover_medium ?: ""
                                        musicSearch = ""
                                        musicResults = emptyList()
                                        musicSelected = true
                                    }.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (track.album.cover_small != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(track.album.cover_small),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center) { Text("♫", fontSize = 16.sp) }
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(track.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(track.artist.name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                if (musicLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                }
            }

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
                            albumArt = albumArtUrl,
                            content = content
                        )
                        val ok = repo.saveJournal(journal, imageBase64)
                        if (ok) {
                            message = "✅ Jurnal tersimpan!"
                            title = ""; content = ""; mood = ""; music = ""
                            musicSearch = ""; albumArtUrl = ""; musicSelected = false
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

// Deezer API response models
data class MusicResponse(val data: List<TrackResult> = emptyList())
data class TrackResult(
    val title: String = "",
    val artist: ArtistResult = ArtistResult(),
    val album: AlbumResult = AlbumResult()
)
data class ArtistResult(val name: String = "")
data class AlbumResult(
    val cover_small: String? = null,
    val cover_medium: String? = null
)
