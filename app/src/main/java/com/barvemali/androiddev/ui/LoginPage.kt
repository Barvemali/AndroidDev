package com.barvemali.androiddev.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.barvemali.androiddev.controller.TeacherController
import com.barvemali.androiddev.controller.getCurrentStudentProfile
import com.barvemali.androiddev.controller.loginController
import com.barvemali.androiddev.controller.teacherLoginController
import com.barvemali.androiddev.ui.theme.medium
import kotlinx.coroutines.launch

@Preview
@Composable
fun LoginPage(navController: NavController = rememberNavController()) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            label = { Text(text = "学号/工号") },
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            label = { Text(text = "密码") },
            visualTransformation = PasswordVisualTransformation(),
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(vertical = 25.dp))
        LoginButton(username, password, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginButton(username : String, password : String, navController: NavController){
    val composableScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }

        Button(
            onClick = {
                composableScope.launch {
                    if (loginController(username, password)){
                        val currentStudent = getCurrentStudentProfile()
                        navController.navigate("home/" + currentStudent.sname + "/" + username){
                            popUpTo("welcome"){
                                inclusive = true
                            }
                        }
                    } else if (teacherLoginController(username, password)){
                        val currentTeacher = TeacherController().findTeacherById(username.toInt())
                        navController.navigate("teacher/" + currentTeacher.tname + "/" + username){
                            popUpTo("home"){
                                inclusive = true
                            }
                        }
                    } else {
                        openDialog.value = true
                    }
                }
            },
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .clip(medium)
        ) {
            Text(
                text = "登录",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (openDialog.value){
            BasicAlertDialog(
                onDismissRequest = { openDialog.value = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "用户名或密码错误",
                        )
                    }
                }
            }
        }

}