package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Course
import com.barvemali.androiddev.model.service.CourseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CourseController {
    val service = CourseService()

    fun getAllCourse(): List<Course> = runBlocking{
        lateinit var courses: List<Course>

        val job = launch( Dispatchers.IO ){
            courses = service.courseConnect()
        }
        job.join()

        courses
    }

    fun getCourse(id: String): Course{
        return service.findCourseById(id)
    }
}