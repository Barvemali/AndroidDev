package com.barvemali.androiddev.model.service

import android.util.Log
import com.barvemali.androiddev.model.adaptor.ApplyJson
import com.barvemali.androiddev.model.adaptor.ApplyJsonAdaptor
import com.barvemali.androiddev.model.entity.Apply
import com.barvemali.androiddev.ui.courseController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ApplyService {
    val JSON = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    var applies: List<Apply> = emptyList()
    val adaptor = ApplyJsonAdaptor()

    fun findApplies(): List<Apply>
    {
        val applyJsons : List<ApplyJson>
        var string = ""
        runBlocking {
            val url = "http://10.0.2.2:8080/apply/list"
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()

            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        string = response.body.string()
                    }
                })
        }
        while(string == ""){
            continue
        }
//        val moshi = Moshi.Builder().add(ApplyJsonAdaptor()).build()
//        val parameterizedType = Types.newParameterizedType(List::class.java, Apply::class.java)
//        val adapter = moshi.adapter<List<Apply>>(parameterizedType)
//        applies = adapter.fromJson(string)!!
        applyJsons = Json.decodeFromString(string)
        var result: List<Apply> = emptyList()
        for (json in applyJsons){
            result = result.plus(adaptor.fromJson(json))
        }
        applies = result
        return result
    }

    fun post(json: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("post", json)
            val body = json.toRequestBody(JSON)
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
        }
    }

    fun teacherPass(id: Int, pass: Boolean, reason: String){
        val mediaType = "application/json".toMediaTypeOrNull()

        val json = JSONObject()
        json.put("id", id)
        json.put("pass", pass)
        json.put("reason", reason)
        val requestBodyJson = json.toString()
        Log.d("teacherPass", requestBodyJson)

        CoroutineScope(Dispatchers.IO).launch {
            val body = requestBodyJson.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/teacherpass")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
        }
    }

    fun managerPass(id: Int, pass: Boolean, reason: String){
        val mediaType = "application/json".toMediaTypeOrNull()

        val json = JSONObject()
        json.put("id", id)
        json.put("pass", pass)
        json.put("reason", reason)
        val requestBodyJson = json.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val body = requestBodyJson.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/managerpass")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
        }
    }

    fun confirm(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject()
            val requestBodyJson = json.toString()
            val body = requestBodyJson.toRequestBody()
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/confirm/$id")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
        }
    }

    fun studentSearchByCourse(courseName: String): List<Apply>{
        val result: ArrayList<Apply> = ArrayList()
        val courses = courseController.searchByCourseName(courseName)
        val courseId : ArrayList<String> = ArrayList()
        for (course in courses){
            courseId.add(course.id)
        }
        for (apply in applies){
            if (courseId.contains(apply.courseid)){
                result.add(apply)
            }
        }
        return result
    }

    fun teacherSearchByCourse(id: Int, course: String): List<Apply>{
        var string = ""
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/teachersearchbycourse/" + id.toString() + "/" + course)
                .method("GET", null)
                .build()

            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        string = response.body.string()
                    }
                })

        }
        while(string == ""){
            continue
        }
        val moshi = Moshi.Builder().build()
        val parameterizedType = Types.newParameterizedType(List::class.java, Apply::class.java)
        val adapter = moshi.adapter<List<Apply>>(parameterizedType)
        val applies = adapter.fromJson(string)!!
        return applies
    }

    fun searchByPerson(teacher: String, student: String): List<Apply>{
        var string = ""
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/apply/searchbyperson/" + teacher + "/" + student)
                .method("GET", null)
                .build()

            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        string = response.body.string()
                    }
                })

        }
        while(string == ""){
            continue
        }
        val moshi = Moshi.Builder().build()
        val parameterizedType = Types.newParameterizedType(List::class.java, Apply::class.java)
        val adapter = moshi.adapter<List<Apply>>(parameterizedType)
        val applies = adapter.fromJson(string)!!
        return applies
    }
}