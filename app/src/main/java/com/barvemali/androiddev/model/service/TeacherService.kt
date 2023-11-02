package com.barvemali.androiddev.model.service

import com.barvemali.androiddev.model.entity.Teacher
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class TeacherService {
    suspend fun findTeacherById(id: Int): Teacher {
            val url = "http://10.0.2.2:8080/teacher/" + id.toString()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            OkHttpClient().newCall(request).execute().use{response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(Teacher::class.java)
                return adapter.fromJson(response.body.string())!!
            }
    }
}