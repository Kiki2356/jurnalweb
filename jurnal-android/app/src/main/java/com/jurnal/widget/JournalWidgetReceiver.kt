package com.jurnal.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.jurnal.app.R
import com.jurnal.ui.WriteWidgetActivity

class JournalWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, WriteWidgetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_tulis, pi)

            val prefs = context.getSharedPreferences("jurnal_widget", Context.MODE_PRIVATE)
            val preview = prefs.getString("preview", "Buka app untuk memuat jurnal") ?: ""
            val date = prefs.getString("preview_date", "") ?: ""
            views.setTextViewText(R.id.widget_preview, preview)
            views.setTextViewText(R.id.widget_date, date)

            manager.updateAppWidget(id, views)
        }
    }

    companion object {
        fun refreshWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, JournalWidgetReceiver::class.java)
            )
            val provider = JournalWidgetReceiver()
            provider.onUpdate(context, manager, ids)
        }
    }
}
