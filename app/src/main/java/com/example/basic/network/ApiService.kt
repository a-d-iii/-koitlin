package com.example.basic.network

import com.example.basic.ClassEntry
import com.example.basic.Timetable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

object ApiService {
    private val client = OkHttpClient()
    private const val BASE_URL = "http://localhost:8000"

    suspend fun getSemesters(): List<String> = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url("$BASE_URL/semesters").build()
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: return@withContext emptyList()
                val arr = JSONArray(body)
                List(arr.length()) { arr.getString(it) }
            }
        }.getOrDefault(emptyList())
    }

    suspend fun getTimetable(semId: String): Timetable = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url("$BASE_URL/timetable?semSubId=$semId").build()
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: return@withContext emptyMap()
                parseTimetable(JSONObject(body))
            }
        }.getOrDefault(emptyMap())
    }

    private fun parseTimetable(obj: JSONObject): Timetable {
        val map = mutableMapOf<String, List<ClassEntry>>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val day = keys.next()
            val arr = obj.optJSONArray(day) ?: JSONArray()
            val list = mutableListOf<ClassEntry>()
            for (i in 0 until arr.length()) {
                val c = arr.getJSONObject(i)
                val time = c.optString("time")
                val parts = time.split("-", limit = 2)
                val entry = ClassEntry(
                    course = c.optString("course_name", c.optString("course_code")),
                    faculty = c.optString("faculty"),
                    start = parts.getOrNull(0)?.trim() ?: "",
                    end = parts.getOrNull(1)?.trim() ?: "",
                    room = c.optString("venue")
                )
                list.add(entry)
            }
            map[day] = list
        }
        return map
    }
}
