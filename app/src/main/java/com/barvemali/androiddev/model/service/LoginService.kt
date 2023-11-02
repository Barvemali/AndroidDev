package com.barvemali.androiddev.model.service

import android.annotation.SuppressLint
import android.util.Log
import com.barvemali.androiddev.model.entity.Student
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoginService{
    lateinit var currentStudent: Student

    suspend fun fetchStudentList(): List<Student>{
            val url = "http://10.0.2.2:8080/student/list"
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val moshi = Moshi.Builder().build()
                val parameterizedType = Types.newParameterizedType(List::class.java, Student::class.java)
                val adapter = moshi.adapter<List<Student>>(parameterizedType)
                return adapter.fromJson(response.body.source())!!
            }
    }

    fun loginVerify(id: String, password: String): Boolean = runBlocking{
        lateinit var students: List<Student>
        var isSuccess = false

        val job = launch(Dispatchers.IO){
            students = fetchStudentList()
            currentStudent = students.find { it.studentid.toString() == id } ?: return@launch
            isSuccess = currentStudent.spassword == password
        }
        job.join()

        isSuccess
    }

    fun findStudentById(id: Int): Student {
        var string = ""
        runBlocking {
            val url = "http://10.0.2.2:8080/student/" + id.toString()
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
        val adapter = moshi.adapter(Student::class.java)
        val student = adapter.fromJson(string)!!
        return student
    }
}
