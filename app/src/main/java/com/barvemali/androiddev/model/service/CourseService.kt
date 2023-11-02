package com.barvemali.androiddev.model.service

import android.util.Log
import com.barvemali.androiddev.model.entity.Course
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CourseService {
    suspend fun courseConnect(): List<Course>{
            val url = "http://10.0.2.2:8080/course/list"
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            OkHttpClient().newCall(request).execute().use{response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val string = response.body.string()
                val moshi = Moshi.Builder().build()
                val parameterizedType = Types.newParameterizedType(List::class.java, Course::class.java)
                val adapter = moshi.adapter<List<Course>>(parameterizedType)
                return adapter.fromJson(string)!!
            }
    }

    fun findCourseById(id: String): Course {
        var string = ""
        runBlocking {
            val url = "http://10.0.2.2:8080/course/" + id
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            OkHttpClient().newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        string = response.body.string()
                        Log.d("bbb", string)
                    }
                })
        }
        Log.d("aaa", string)
        while(string == ""){
            continue
        }
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Course::class.java)
        val course = adapter.fromJson(string)!!
        return course
    }
}