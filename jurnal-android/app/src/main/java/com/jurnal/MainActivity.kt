package com.jurnal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jurnal.data.TokenManager
import com.jurnal.ui.MainScreen
import com.jurnal.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(this)

        var sharedText = ""
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        }

        setContent {
            var showSettings by remember { mutableStateOf(!tokenManager.isConfigured) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showSettings) {
                        SettingsScreen(tokenManager) { showSettings = false }
                    } else {
                        MainScreen(tokenManager, sharedText)
                    }
                }
            }
        }
    }
}
