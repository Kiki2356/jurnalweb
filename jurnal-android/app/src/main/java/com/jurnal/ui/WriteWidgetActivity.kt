package com.jurnal.ui

import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.jurnal.data.Journal
import com.jurnal.data.JournalRepository
import com.jurnal.widget.JournalWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

val widgetMoods = listOf("😊", "😌", "🥰", "😢", "😡", "😴", "🤔", "🔥")

class WriteWidgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("jurnal_widget", MODE_PRIVATE)
        val token = prefs.getString("gh_token", "") ?: ""
        val repo = prefs.getString("gh_repo", "") ?: ""

        if (token.isBlank() || repo.isBlank()) {
            // Fallback: open settings
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            MaterialTheme {
                WriteWidgetSheet(
                    token = token,
                    repo = repo,
                    onSaved = {
                        JournalWidgetReceiver.refreshWidget(this@WriteWidgetActivity)
                        finish()
                    },
                    onClose = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteWidgetSheet(token: String, repo: String, onSaved: () -> Unit, onClose: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✏️ Tulis Jurnal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("Apa yang kamu pikirkan?", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = { Text("Ada apa hari ini?...") },
                maxLines = 12
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                widgetMoods.forEach { m ->
                    FilterChip(
                        selected = mood == m,
                        onClick = { mood = if (mood == m) "" else m },
                        label = { Text(m, fontSize = 16.sp) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (text.isBlank()) return@Button
                    saving = true
                    scope.launch {
                        try {
                            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date())
                            val journal = Journal(
                                title = text.split(" ").take(5).joinToString(" "),
                                date = now,
                                mood = mood.ifEmpty { "-" },
                                content = text
                            )
                            // Save via coroutine on IO
                            val ok = withContext(Dispatchers.IO) {
                                JournalRepository(token, repo).saveJournal(journal, null)
                            }
                            if (ok) onSaved() else saving = false
                        } catch (_: Exception) { saving = false }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !saving && text.isNotBlank()
            ) {
                Text(if (saving) "Menyimpan..." else "💾 Simpan")
            }
        }
    }
}
