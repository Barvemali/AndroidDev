package com.barvemali.androiddev.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.barvemali.androiddev.controller.ApplyController
import com.barvemali.androiddev.controller.CourseController
import com.barvemali.androiddev.controller.findStudentById
import com.barvemali.androiddev.model.entity.Apply
import kotlinx.coroutines.launch

sealed class TeacherScreen(
    val route: String,
    val icon: ImageVector,
    val text: String
){
    object Course : TeacherScreen("task", Icons.Filled.Menu, "待办事项")
    object Profile : TeacherScreen("profile", Icons.Filled.Person, "我的")
}

private val items = listOf(
    TeacherScreen.Course,
    TeacherScreen.Profile
)

private val applyController = ApplyController()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun TeacherPage(mainController: NavController, name: String?, id: String?){
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach{ screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.text) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route){
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TeacherScreen.Course.route, Modifier.padding(innerPadding)){
            composable(TeacherScreen.Course.route){ TaskPage(id) }
            composable(TeacherScreen.Profile.route){ TeacherProfile(
                mainController = mainController,
                name = name,
                id = id
            ) }
        }
        scope.launch { snackbarHostState.showSnackbar("登录成功") }
    }
}

@Composable
fun TaskPage(id: String?){
    val applies = ApplyController().getApplies()
    val courseController = CourseController()
    var refresh by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (refresh){
            applies.forEach{
                if (!it.isfinish){
                    if (id != null) {
                        Log.d("get", "get")
                        val course = courseController.getCourse(it.courseid)
                        var level = 0
                        if (course.teacher == id.toInt() && it.isteacherpass == null){
                            level = 1
                        } else if (course.manager == id.toInt() && it.ismanagerpass == null){
                            level = 2
                        }
                        if (level != 0){
                            Request(it, level, { refresh = false }, { refresh = true })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Request(apply: Apply, level: Int, refreshA: () -> Unit, refreshB:() -> Unit){
    val checkedState = remember { mutableStateOf(true) }
    val openDialog = remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    Card(
        onClick = { openDialog.value = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = CourseController().getCourse(apply.courseid).cname, style = MaterialTheme.typography.headlineSmall)
            Text(text = findStudentById(apply.studentid).sname)
        }
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
                    Text(text = "详细信息")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it }
                        )
                        Text(text = "审核通过")
                    }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text(text = "原因") }
                    )
                    Button(
                        onClick = {
                            when (checkedState.value) {
                                true -> when (level) {
                                    1 -> apply.id?.let { ApplyController().teacherPass(it, true, text) }
                                    2 -> apply.id?.let { ApplyController().managerPass(it, true, text) }
                                }

                                false -> when (level) {
                                    1 -> apply.id?.let { ApplyController().teacherPass(it, false, text) }
                                    2 -> apply.id?.let { ApplyController().managerPass(it, false, text) }
                                }
                            }
                            openDialog.value = false
                            refreshA()
                            refreshB()
                        }) {
                        Text(text = "确定")
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherProfile(mainController: NavController, name: String?, id: String?){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "page"){
        composable("page"){ TeacherProfileScreen(
            historyController = navController,
            navController = mainController,
            name = name,
            id = id
        ) }
        composable("history"){
            if (id != null) {
                TeacherHistory(id.toInt(), name)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(historyController: NavController, navController: NavController, name: String?, id: String?){
    val openDialog = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)){
                Column {
                    if (name != null && id != null) {
                        Text(text = name, style = MaterialTheme.typography.headlineSmall)
                        Text(text = id)
                    }

                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { historyController.navigate("history") }) {
                Text(text = "历史记录")
            }
            Button(onClick = { openDialog.value = true }) {
                Text(text = "注销")
            }
        }
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "确定要注销吗？")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {
                            openDialog.value = false
                            navController.navigate("welcome") {
                                popUpTo("teacher/{name}/{id}") { inclusive = true }
                            }
                        }) {
                            Text(text = "确定")
                        }
                        Button(onClick = { openDialog.value = false }) {
                            Text(text = "取消")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherHistory(id: Int, name: String?){
    var teacherHistoryApplies by remember { mutableStateOf(applyController.getApplies()) }
    var text by rememberSaveable { mutableStateOf("") }
    var mode by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    //var refresh by remember { mutableStateOf(true) }
    val courseController = CourseController()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box {
            Row {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Filled.Search, "search",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.Menu, contentDescription = "")
                        }
                    },
                    placeholder = {
                        Text(text = "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            //refresh = false
                            if (name != null){
                                when(mode){
                                    "course" -> teacherHistoryApplies = applyController.teacherSearchByCourse(id, text)
                                    "student" -> teacherHistoryApplies = applyController.searchByPerson(name, text)
                                }
                            }
                            //refresh = true
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    )
                )
                Box{
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        DropdownMenuItem(text = { Text(text = "按课程名称搜索")} , onClick = {
                            mode = "course"
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text(text = "按学生名字搜索") }, onClick = {
                            mode = "student"
                            expanded = false
                        })
                    }
                }
            }
        }
        Log.d("TeacherHistory", teacherHistoryApplies.toString())
        teacherHistoryApplies.forEach{
            val course = courseController.getCourse(it.courseid)
            if((course.teacher == id && it.isteacherpass != null)||(course.manager == id && it.ismanagerpass != null)){
                HistoricalApproval(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalApproval(apply: Apply){

    val course = CourseController().getCourse(apply.courseid)
    val openDialog = remember { mutableStateOf(false) }
    val student = findStudentById(apply.studentid)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { openDialog.value = true }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
        ){
            Column {
                Text(text = course.cname, style = MaterialTheme.typography.headlineSmall)
                Text(text = student.sname)
            }
        }
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
                    Text(text = "课程名称", style = MaterialTheme.typography.titleLarge)
                    Text(text = course.cname)
                    Text(text = "申请学生", style = MaterialTheme.typography.titleLarge)
                    Text(text = student.sname)
                    Text(text = "原因", style = MaterialTheme.typography.titleLarge)
                    Text(text = apply.reason)
                    Text(text = "主讲教师意见", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = when (apply.isteacherpass) {
                            true -> "同意"
                            false -> "驳回"
                            else -> ""
                        }
                    )
                    Text(text = "主管教师意见", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = when (apply.isteacherpass) {
                            true -> "同意"
                            false -> "驳回"
                            else -> ""
                        }
                    )
                }
            }
        }
    }
}