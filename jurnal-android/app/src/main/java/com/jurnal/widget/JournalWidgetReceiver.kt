package com.jurnal.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.jurnal.app.R
import com.jurnal.ui.WriteWidgetActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL

class JournalWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, manager, id)
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // Tap to open write activity
        val intent = Intent(context, WriteWidgetActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_tulis, pi)

        // Load preview from API
        val prefs = context.getSharedPreferences("jurnal_widget", Context.MODE_PRIVATE)
        val savedPreview = prefs.getString("preview", "") ?: ""
        val savedDate = prefs.getString("preview_date", "") ?: ""

        if (savedPreview.isNotBlank()) {
            views.setTextViewText(R.id.widget_preview, savedPreview)
            views.setTextViewText(R.id.widget_date, savedDate)
        } else {
            // Fetch in background
            try {
                val repo = prefs.getString("gh_repo", "")
                if (!repo.isNullOrBlank()) {
                    val json = URL("https://jurnalweb.netlify.app/.netlify/functions/journals").readText()
                    val preview = parsePreview(json)
                    views.setTextViewText(R.id.widget_preview, preview.first)
                    views.setTextViewText(R.id.widget_date, preview.second)
                    prefs.edit().putString("preview", preview.first)
                        .putString("preview_date", preview.second).apply()
                }
            } catch (_: Exception) {}
        }

        manager.updateAppWidget(appWidgetId, views)
    }

    private fun parsePreview(json: String): Pair<String, String> {
        return try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<com.jurnal.data.RawJournal>>() {}.type
            val list: List<com.jurnal.data.RawJournal> = gson.fromJson(json, type)
            if (list.isEmpty()) return Pair("Belum ada jurnal", "")
            val last = list.first()
            val fm = Regex("""^---\s*\n([\s\S]*?)\n---\s*\n([\s\S]*)$""").find(last.content)
            val body = fm?.groupValues?.get(2)?.trim() ?: last.content
            val title = if (fm != null) {
                val h = fm.groupValues[1]
                h.lines().firstOrNull { it.startsWith("title:") }?.substringAfter(":")?.trim()?.replace("\"", "") ?: "Tanpa Judul"
            } else "Tanpa Judul"
            val preview = body.take(80).replace("\n", " ") + if (body.length > 80) "..." else ""
            Pair(preview, title.take(30))
        } catch (_: Exception) {
            Pair("Gagal memuat", "")
        }
    }

    companion object {
        fun refreshWidget(context: Context) {
            val prefs = context.getSharedPreferences("jurnal_widget", Context.MODE_PRIVATE)
            prefs.edit().remove("preview").remove("preview_date").apply()
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, JournalWidgetReceiver::class.java)
            )
            for (id in ids) {
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                val intent = Intent(context, WriteWidgetActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.widget_tulis, pi)
                manager.updateAppWidget(id, views)
            }
        }
    }
}
