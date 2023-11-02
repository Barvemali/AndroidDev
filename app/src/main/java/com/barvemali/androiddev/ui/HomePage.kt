package com.barvemali.androiddev.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
import com.barvemali.androiddev.controller.TeacherController
import com.barvemali.androiddev.model.entity.Apply
import com.barvemali.androiddev.model.entity.Course
import com.barvemali.androiddev.ui.theme.h1
import kotlinx.coroutines.launch
import java.io.File

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val text: String
){
    data object Course : Screen("course", Icons.Filled.Menu, "课程列表")
    data object Process : Screen("process", Icons.Filled.AccessTime, "审核进度")
    data object Profile : Screen("profile", Icons.Filled.Person, "我的")
}

private val items = listOf(
    Screen.Course,
    Screen.Process,
    Screen.Profile
)
private val applyController = ApplyController()
private val courseController = CourseController()
private val teacherController = TeacherController()

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun Homepage(mainController: NavController, name: String?, id: String?){
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
                        icon = { Icon(screen.icon, contentDescription = null)},
                        label = { Text(screen.text)},
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
        NavHost(navController = navController, startDestination = Screen.Course.route, Modifier.padding(innerPadding)){
            composable(Screen.Course.route){
                if (id != null) {
                    HomeScreen(id.toInt())
                }
            }
            composable(Screen.Process.route){ Process(id) }
            composable(Screen.Profile.route){ Profile(mainController, name, id) }
        }
        scope.launch { snackbarHostState.showSnackbar("登录成功") }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HomeScreen(userid: Int){
    Column {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar()
            BloomInfoList(userid)
        }
    }
}

@Composable
fun SearchBar(){
    var text by rememberSaveable { mutableStateOf("") }
    Box {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(BorderStroke(1.dp, Color.Black)),
            leadingIcon = {
                Icon(Icons.Filled.Search, "search",
                    modifier = Modifier.size(18.dp)
                )
            },
            placeholder = {
                Text(text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Course(course: Course, userid: Int){
    val openDialog = remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }
    val teacher = TeacherController().findTeacherById(course.teacher)
    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        uri = it
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { openDialog.value = true }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
        ){
            Column {
                Text(text = course.cname, style = MaterialTheme.typography.headlineSmall)
                Text(text = teacher.tname)
            }
        }
    }

    if (openDialog.value){
        AlertDialog(
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
                    TextField(
                        label = { Text(text = "申请原因") },
                        value = reason,
                        onValueChange = { reason = it }
                    )
                    Button(
                        onClick = {
                            launcher.launch("*/*")
                        }) {
                        Text(text = "选择证明材料")
                    }
                    Button(
                        onClick = {
                            val file = uri?.path?.let { File(it) }
                            openDialog.value = false
                            applyController.createApply(Apply(applyController.getMaxId()+1, course.id, userid, reason, null, null, null, false, "", ""))
                        }) {
                        Text(text = "确定")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BloomInfoList(userid: Int){
    val courses = CourseController().getAllCourse()
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "选择要申请的课程",
                style = h1,
                color = Color.Gray,
                modifier = Modifier.paddingFromBaseline(top = 40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            item {
                Spacer(modifier = Modifier)
            }
            courses.forEach{
                item{
                    Course(it, userid)
                }
            }
        }
    }
}

@Composable
fun Process(id: String?){
    val applies = applyController.getApplies()
    var refresh by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (refresh){
            applies.forEach{
                if (it.studentid == id?.toInt() && !it.isfinish){
                    ProcessingCourse(it, {refresh = false}, {refresh = true})
                }
            }
        }
    }
}

@Composable
fun ProcessingCourse(apply: Apply, refreshA: () -> Unit, refreshB: () -> Unit){
    val course = courseController.getCourse(apply.courseid)
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
        ){
            Row {
                Column {
                    Text(text = course.cname, style = MaterialTheme.typography.headlineSmall)
                    Text(text = teacherController.findTeacherById(course.teacher).tname)
                    if (apply.isteacherpass == null){
                        LinearProgressIndicator(progress = 1f/3f)
                        Text(text = "主讲教师审核中")
                    } else if (apply.ismanagerpass == null){
                        LinearProgressIndicator(progress = 2f/3f)
                        Text(text = "主管教师审核中")
                    } else {
                        LinearProgressIndicator(progress = 1f)
                        Text(text = "审批完成")
                    }
                }
                if((apply.isteacherpass == true && apply.ismanagerpass == true)|| apply.isteacherpass == false || apply.ismanagerpass == false){
                    Button(onClick = {
                        applyController.confirm(apply.id)
                        refreshA()
                        refreshB()
                    }) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }
}

@Composable
fun Profile(mainController: NavController, currentStudent: String?, id: String?){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "page"){
        composable("page"){ ProfileScreen(
            historyController = navController,
            navController = mainController,
            currentStudent = currentStudent,
            id = id
        ) }
        composable("history"){
            if (id != null) {
                History(id.toInt(), currentStudent)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(historyController: NavController, navController: NavController, currentStudent: String?, id: String?){
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
                    if (currentStudent != null && id != null) {
                        Text(text = currentStudent, style = MaterialTheme.typography.headlineSmall)
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
        AlertDialog(
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
                            navController.navigate("welcome"){
                                popUpTo("home/{name}/{id}"){
                                    inclusive = true
                                }
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
fun History(id: Int, currentStudent: String?){
    var historyApplies by remember { mutableStateOf(ApplyController().getApplies()) }
    var text by rememberSaveable { mutableStateOf("") }
    var mode by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
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
                    modifier = Modifier.fillMaxWidth(),
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
                            Log.d("mode", mode)
                            Log.d("text", text)
                            if (currentStudent != null){
                                when(mode){
                                    "course" -> historyApplies = applyController.studentSearchByCourse(id, text)
                                    "teacher" -> historyApplies = applyController.searchByPerson(text, currentStudent)
                                }
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
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
                        DropdownMenuItem(text = { Text(text = "按教师名称搜索") }, onClick = {
                            mode = "teacher"
                            expanded = false
                        })
                    }
                }
            }
        }
        historyApplies.forEach{
            if (it.studentid == id && it.isfinish){
                HistoricalCourse(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalCourse(apply: Apply){

    val course = CourseController().getCourse(apply.courseid)
    val openDialog = remember { mutableStateOf(false) }

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
                Text(text = TeacherController().findTeacherById(course.teacher).tname)
            }
        }
    }

    if (openDialog.value){
        AlertDialog(
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
                    Text(text = "原因", style = MaterialTheme.typography.titleLarge)
                    Text(text = apply.reason)
                    Text(text = "主讲教师意见", style = MaterialTheme.typography.titleLarge)
                    Text(text = when(apply.isteacherpass){
                        true -> "同意"
                        false -> "驳回"
                        else -> ""
                    })
                    if(apply.teacherreason != ""){
                        Text(text = "主讲教师理由", style = MaterialTheme.typography.titleLarge)
                        Text(text = apply.teacherreason)
                    }
                    Text(text = "主管教师意见", style = MaterialTheme.typography.titleLarge)
                    Text(text = when(apply.ismanagerpass){
                        true -> "同意"
                        false -> "驳回"
                        else -> ""
                    })
                    if(apply.managerreason != ""){
                        Text(text = "主讲教师理由", style = MaterialTheme.typography.titleLarge)
                        Text(text = apply.managerreason)
                    }
                }
            }
        }
    }
}