package com.jurnal.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class JournalWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if (intent != null) {
                val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                views.setTextViewText(android.R.id.text1, "📓 Jurnal Pribadi — Tap untuk menulis")
                views.setOnClickPendingIntent(android.R.id.text1, pi)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
