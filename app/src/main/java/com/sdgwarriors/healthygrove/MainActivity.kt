package com.sdgwarriors.healthygrove

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdgwarriors.healthygrove.ui.theme.MyApplicationTheme
import androidx.compose.material3.MaterialTheme as MaterialTheme3

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme3.colorScheme.background
                ) {
                    val uriHandler = LocalUriHandler.current
                    Scaffold(
                        topBar = { TopAppBar(
                            title = { Text(
                                "HealthyGrove",
                                fontFamily = MaterialTheme.typography.body2.fontFamily,
                                fontSize = 24.sp
                            ) },
                            backgroundColor = MaterialTheme.colors.background,
                            elevation = 0.dp,
                            navigationIcon = {
                                Image(
                                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            },
                        ) },
                        content = {
                            LazyColumn(modifier = Modifier.fillMaxWidth()){
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .clickable {
                                                startActivity(
                                                    Intent(context, CamActivity::class.java)
                                                )
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        backgroundColor =
                                        if (isSystemInDarkTheme())
                                            Color(0xFF005300)
                                        else
                                            Color(0xFF73ff5a),
                                        contentColor =
                                        if (isSystemInDarkTheme())
                                            Color(0xFFe0e3e3)
                                        else
                                            Color(0xFF191c1d),
                                    ) {
                                        Column {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Image(
                                                    painter = painterResource(R.mipmap.detection_foreground),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .fillMaxWidth()
                                                        .height(180.dp)
                                                        .clip(RoundedCornerShape(16.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    "Check your plant's health",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.W600,
                                                    modifier = Modifier.padding(0.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    "Select a picture or take a photo of a plant of which you want to check/detect the disease",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.W300,
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 16.dp
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .clickable {
                                                uriHandler.openUri("https://icar.org.in")
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        backgroundColor =
                                        if (isSystemInDarkTheme())
                                            Color(0xFF005300)
                                        else
                                            Color(0xFF73ff5a),

                                        contentColor =
                                        if (isSystemInDarkTheme())
                                            Color(0xFFe0e3e3)
                                        else
                                            Color(0xFF191c1d),
                                    ) {
                                        Column {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Image(
                                                    painter = painterResource(R.mipmap.community_foreground),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .fillMaxWidth(1f)
                                                        .height(260.dp)
                                                        .clip(RoundedCornerShape(16.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    "Community Panel",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.W600,
                                                    modifier = Modifier.padding(0.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    "A forum to have an open conversation and discussing ideas about green life",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.W300,
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 16.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { startActivity(Intent(
                                    this,
                                    BotActivity::class.java
                                ) .putExtra("disease", "none")) },
                                shape = RoundedCornerShape(
                                    topEnd = 16.dp,
                                    topStart = 16.dp,
                                    bottomEnd = 4.dp,
                                    bottomStart = 16.dp,
                                ),
                                backgroundColor =
                                    if(!isSystemInDarkTheme())
                                        Color(0xFFE8DEF8)
                                    else
                                        Color(0xFF4A4458)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_message_circle),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onBackground
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.End,
                    )
                }
            }
        }
    }
}