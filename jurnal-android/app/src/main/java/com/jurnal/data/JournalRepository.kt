package com.jurnal.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class RawJournal(val name: String = "", val content: String = "")

class JournalRepository(private val token: String, private val repo: String) {

    private val baseUrl = "https://api.github.com/repos/$repo"
    private val gson = Gson()

    suspend fun fetchJournals(): List<Journal> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://jurnalweb.netlify.app/.netlify/functions/journals")
            val json = url.readText()
            val type = object : TypeToken<List<RawJournal>>() {}.type
            val rawList: List<RawJournal> = gson.fromJson(json, type)
            rawList.map { parseFrontmatter(it.content) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveJournal(journal: Journal, imageBase64: String?): Boolean = withContext(Dispatchers.IO) {
        try {
            val now = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US).format(Date())
            val fileName = "$now.md"

            var imageUrl = ""
            if (imageBase64 != null) {
                val ext = imageBase64.substringAfter("image/").substringBefore(";").ifEmpty { "png" }
                val imgName = "${now}.$ext"
                val imgB64 = imageBase64.substringAfter(",")
                uploadFile("journals/images/$imgName", imgB64, "gambar: $imgName")
                imageUrl = "https://raw.githubusercontent.com/$repo/main/journals/images/$imgName"
            }

            val md = buildString {
                appendLine("---")
                appendLine("title: \"${journal.title}\"")
                appendLine("date: ${journal.date}")
                appendLine("tags: []")
                appendLine("mood: \"${journal.mood}\"")
                appendLine("music: \"${journal.music}\"")
                appendLine("image: \"$imageUrl\"")
                appendLine("---")
                appendLine()
                append(journal.content)
            }

            val b64 = Base64.getEncoder().encodeToString(md.toByteArray())
            uploadFile("journals/$fileName", b64, "jurnal: ${journal.title}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun uploadFile(path: String, base64: String, message: String) {
        val url = URL("$baseUrl/contents/$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Authorization", "token $token")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        val body = """{"message":"$message","content":"$base64","branch":"main"}"""
        conn.outputStream.write(body.toByteArray())
        if (conn.responseCode !in 200..299) {
            throw Exception("GitHub API error: ${conn.responseCode}")
        }
        conn.disconnect()
    }

    private fun parseFrontmatter(raw: String): Journal {
        var title = ""
        var date = ""
        var mood = ""
        var music = ""
        var albumArt = ""
        var image = ""
        var content = raw

        val fmMatch = Regex("""^---\s*\n([\s\S]*?)\n---\s*\n([\s\S]*)$""").find(raw)
        if (fmMatch != null) {
            val header = fmMatch.groupValues[1]
            content = fmMatch.groupValues[2]
            for (line in header.split("\n")) {
                val kv = line.split(":\\s*".toRegex(), limit = 2)
                if (kv.size < 2) continue
                val key = kv[0].trim()
                val value = kv[1].trim().replace("^\"|\"$".toRegex(), "")
                when (key) {
                    "title" -> title = value
                    "date" -> date = value
                    "mood" -> mood = value
                    "music" -> music = value
                    "album_art" -> albumArt = value
                    "image" -> image = value
                }
            }
        }

        return Journal(
            title = title.ifEmpty { "Tanpa Judul" },
            date = date,
            mood = mood,
            music = music,
            albumArt = albumArt,
            image = image,
            content = content.trim()
        )
    }
}
