package com.barvemali.androiddev.model.service

import com.barvemali.androiddev.model.entity.Teacher
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class TeacherService {
    suspend fun findTeacherById(id: Int): Teacher {
        val url = "http://10.0.2.2:8080/teacher/" + id.toString()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()

        return withContext(Dispatchers.IO) {
            var result: Teacher
            OkHttpClient().newCall(request).execute().use{ response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(Teacher::class.java)
                result = adapter.fromJson(response.body.string())!!
            }
            return@withContext result
        }
    }
}