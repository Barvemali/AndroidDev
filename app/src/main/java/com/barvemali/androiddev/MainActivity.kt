package com.barvemali.androiddev

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barvemali.androiddev.model.entity.Student
import com.barvemali.androiddev.ui.Homepage
import com.barvemali.androiddev.ui.LoginPage
import com.barvemali.androiddev.ui.TeacherPage
import com.barvemali.androiddev.ui.WelComePage
import com.barvemali.androiddev.ui.theme.AndroidDevTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidDevTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidDevTheme {
        AppNavigation()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf(Student("name", 123, "password")) }
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ){
        composable("welcome"){
            WelComePage(navController = navController)
        }
        composable("login"){
            LoginPage(navController = navController)
        }
        composable("home/{name}/{id}"){ backStackEntry ->
            Homepage(
                mainController = navController,
                backStackEntry.arguments?.getString("name"),
                backStackEntry.arguments?.getString("id")
            )
        }
        composable("teacher/{name}/{id}"){ backStackEntry ->
            TeacherPage(
                mainController = navController,
                backStackEntry.arguments?.getString("name"),
                backStackEntry.arguments?.getString("id")
            )}
    }
}