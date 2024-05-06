package com.barvemali.androiddev.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.barvemali.androiddev.R
import com.barvemali.androiddev.ui.theme.Pink100
import com.barvemali.androiddev.ui.theme.medium

@Composable
fun WelComePage(navController: NavController){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Pink100)
    ){
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.ic_light_welcome_bg),
            contentDescription = "welcome_bg",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        WelcomeContent(navController)
    }
}

@Composable
fun WelcomeContent(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LeafImage()
        Spacer(modifier = Modifier.height(48.dp))
        WelcomeTitle()
        Spacer(modifier = Modifier.height(40.dp))
        WelcomeButtons(navController)
    }
}

@Composable
fun LeafImage(){
    Image(
        bitmap = ImageBitmap.imageResource(id = R.drawable.ic_launcher),
        contentDescription = "welcome_illos",
        modifier = Modifier.wrapContentSize()
    )
}

private fun ByteArray.toBitmap(): ImageBitmap? {
    return try {
        // 假设字节数组包含图片数据，这里使用BitmapFactory来解码字节数组为Bitmap
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(this, 0, size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

@Composable
fun WelcomeTitle(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Text(
                text = "A Beautiful Online Submission and Approving Platform",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun WelcomeButtons(navController: NavController){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(medium)
        ) {
            Text(
                text = "登录",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}