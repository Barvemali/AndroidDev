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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.barvemali.androiddev.AppNavigation
import com.barvemali.androiddev.MainActivity
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