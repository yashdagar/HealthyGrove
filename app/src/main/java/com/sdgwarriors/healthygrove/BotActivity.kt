package com.sdgwarriors.healthygrove

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sdgwarriors.healthygrove.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch


class BotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current

            val disease1 = intent.getStringExtra("disease")
            val disease: String
            val str = disease1!!
            val str1 = str.substring(str.indexOf(" ") + 1, str.length)
            disease =
                if(disease1 != "none" && disease1 != "background")
                    if(str.substring(0, str.indexOf(" ")) == str1.substring(0, str1.indexOf(" "))){
                        str1
                    }else{
                        str
                    }
                else
                    str

            val scaffoldState = rememberScaffoldState()

            val text = remember{ mutableStateOf("") }
            val messages: MutableList<Message> = remember { mutableStateListOf() }

            val uriHandler = LocalUriHandler.current


            if(messages.isEmpty() && disease != "none")
                messages.add(Message("Your plant is detected to have $disease.", false){})
            else if(messages.isEmpty() && disease == "none") {
                messages.add(Message("Hi, how can I assist you?", false) {})
                messages.add(Message("For more details:", false) {})
                messages.add(Message("Contact number: 0166 622 1147", false) {})
                messages.add(
                    Message(
                        "For further information about agriculture you can check out these websites",
                        false
                    ) {})
                messages.add(Message(
                    "https://www.livemint.com/industry/agriculture/russiaukraine-conflict-increasing-uncertainty-of-agri-supply-demand-globall-11646929741695.html",
                    false
                ) { uriHandler.openUri("https://www.livemint.com/industry/agriculture/russiaukraine-conflict-increasing-uncertainty-of-agri-supply-demand-globall-11646929741695.html") })
                messages.add(Message(
                    "https://www.livemint.com/industry/agriculture/cotton-output-expected-to-climb-amid-high-global-demand-for-textiles-11644796050930.html",
                    false
                )
                { uriHandler.openUri("https://www.livemint.com/industry/agriculture/cotton-output-expected-to-climb-amid-high-global-demand-for-textiles-11644796050930.html") })
            }
            MyApplicationTheme {

                val coroutineScope = rememberCoroutineScope()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.onBackground
                                    )
                                }
                            },
                            title = {
                                Text(
                                    "Bot",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = MaterialTheme.colors.onBackground,
                                    fontFamily = MaterialTheme.typography.body1.fontFamily
                                )
                            },
                            backgroundColor = MaterialTheme.colors.background,
                            elevation = 0.dp
                        )
                    },
                    content = {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colors.background),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val listState = rememberLazyListState()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    ) {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    items(messages.size) { index ->
                                        MessageItem(
                                            message = messages[index]
                                        )
                                    }
                                }
                            }
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(all = 8.dp)
                            ) {

                                TextField(
                                    value = text.value,
                                    onValueChange = { text.value = it },
                                    Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(32.dp),
                                    trailingIcon = {
                                            IconButton(onClick = {
                                                if (text.value.trim() != "") {
                                                    messages.add(Message(text.value, true) {})
                                                    coroutineScope.launch {
                                                        listState.animateScrollToItem(messages.size - 1)
                                                    }
                                                    var string = text.value.replace("it", disease)
                                                    string = string.replace("this", disease)
                                                    val url =
                                                        "http://api.brainshop.ai/get?bid=164805&key=tRULnOwJLBSke2IZ&uid=1&msg=$string"

                                                    Log.d("idk", url)

                                                    val queue = Volley.newRequestQueue(context)

                                                    val stringRequest = JsonObjectRequest(
                                                        Request.Method.GET, url, null,
                                                        { response ->
                                                            messages.add(
                                                                Message(
                                                                    response.getString(
                                                                        "cnt"
                                                                    ), false
                                                                ) {})
                                                            coroutineScope.launch {
                                                                listState.animateScrollToItem(
                                                                    messages.size - 1
                                                                )
                                                            }
                                                        },
                                                        { Log.d("idk", "error") })

                                                    queue.add(stringRequest)

                                                    text.value = ""
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Default.ArrowForward,
                                                    null,
                                                    tint = MaterialTheme.colors.surface,
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(
                                                            shape = CircleShape,
                                                            brush = Brush.linearGradient(
                                                                colors = listOf(
                                                                    Color(0xFFEB3E07),
                                                                    Color(0xFFE97116),
                                                                    Color(0xFFFFEB3B)
                                                                ),
                                                                start = Offset(0f, 0f),
                                                                end = Offset(100f, 100f)
                                                            )
                                                        )
                                                        .padding(
                                                            top = 5.dp,
                                                            start = 6.dp,
                                                            bottom = 5.dp,
                                                            end = 4.dp
                                                        )
                                                )
                                            }
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        textColor = MaterialTheme.colors.onBackground,
                                    )
                                )
                            }
                        }
                    },
                )
                }
            }
        }
    }

    @Composable
    private fun MessageItem(message: Message) {
        Column(
            modifier = if(message.isMe) Modifier.fillMaxWidth() else Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = if(message.isMe) Alignment.End else Alignment.Start
        ) {
            Row {
                if(message.isMe)
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { message.onclick },
                    backgroundColor =
                    if (message.isMe)
                        MaterialTheme.colors.primaryVariant
                    else
                        if(!isSystemInDarkTheme())
                            Color(0xFFE8DEF8)
                        else
                            Color(0xFF4A4458)
                ) {
                    Text(
                        message.text,
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
                        ),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

data class Message(val text:String, val isMe:Boolean, val onclick:() -> Unit)