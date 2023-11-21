package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Course
import com.barvemali.androiddev.model.service.CourseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CourseController {
    val service = CourseService()
    var courses : List<Course> = emptyList()

    fun getAllCourse(): List<Course> = runBlocking{
        if (courses.isEmpty()){
            val job = launch( Dispatchers.IO ){
                courses = service.courseConnect()
            }
            job.join()
        }
        courses
    }

    fun getCourse(id: String): Course{
        return service.findCourseById(id)
    }

    fun search(name: String): List<Course> {
        val result: ArrayList<Course> = ArrayList()
        for (course in courses){
            if (course.cname.contains(name)){
                result.add(course)
            }
        }
        return result
    }
}