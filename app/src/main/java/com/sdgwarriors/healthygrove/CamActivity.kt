package com.sdgwarriors.healthygrove

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.sdgwarriors.healthygrove.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CamActivity : ComponentActivity() {
    private val modelName = "model.tflite"
    private val labelName = "labelmap.txt"

    private lateinit var classifier: Classifier
    private lateinit var currentPhotoPath: String

    private var width:Int? = null
    private var height:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = Classifier(assets, modelName, labelName, 200)

        setContent {
            Main()
        }
    }

    @Composable
    private fun Main() {

        val context = LocalContext.current

        var error by remember{mutableStateOf(false)}
        val bitmap1 = remember { mutableStateOf<Bitmap?>(null) }
        val title = remember { mutableStateOf<String?>("") }
        val confidence = remember { mutableStateOf<String?>("") }

        val openDialog = remember { mutableStateOf(false) }

        var imageUri by remember { mutableStateOf<Uri?>(null) }

        val launcher1 = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri

            if (Build.VERSION.SDK_INT < 28) {
                bitmap1.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver,imageUri)
                val bmp: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,imageUri)
                    .copy(Bitmap.Config.ARGB_8888, true)
                lifecycleScope.launch(Dispatchers.Default) {
                    val output = classifier.recognizeImage(bmp).firstOrNull()
                    val str = output!!.title
                    val str1 = str.substring(str.indexOf(" ") + 1, str.length)
                    if(str.substring(0, str.indexOf(" ")) == str1.substring(0, str1.indexOf(" "))){
                        title.value = str1
                    }else{
                        title.value = str
                    }
                    error = "healthy" !in output.title
                    confidence.value = "${output.confidence.times(100)}%"
                    if(error && !openDialog.value && output.title != "background")
                        openDialog.value = true
                }
            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, imageUri!!)
                val bmp: Bitmap = ImageDecoder
                    .decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
                bitmap1.value = ImageDecoder.decodeBitmap(source)
                lifecycleScope.launch(Dispatchers.Default) {
                    val output = classifier.recognizeImage(bmp).firstOrNull()
                    val str = output!!.title
                    val str1 = str.substring(str.indexOf(" ") + 1, str.length)
                    if(str.substring(0, str.indexOf(" ")) == str1.substring(0, str1.indexOf(" "))){
                        title.value = str1
                    }else{
                        title.value = str
                    }
                    error = "healthy" !in output.title
                    confidence.value = "${output.confidence.times(100)}%"
                    if(error && !openDialog.value && output.title != "background")
                        openDialog.value = true
                }
            }
        }

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap1.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver,it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver,it)
                bitmap1.value = ImageDecoder.decodeBitmap(source)
            }
        }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                bitmap1.value = getCapturedImage()
                val bmp: Bitmap = getCapturedImage()
                lifecycleScope.launch(Dispatchers.Default) {
                    val output = classifier.recognizeImage(bmp).firstOrNull()
                    val str = output!!.title
                    val str1 = str.substring(str.indexOf(" ") + 1, str.length)
                    if(str != "none" && str != "background") {
                        if (str.substring(0, str.indexOf(" ")) == str1.substring(
                                0,
                                str1.indexOf(" ")
                            )
                        ) {
                            title.value = str1
                        } else {
                            title.value = str
                        }
                    }else{
                        title.value = str
                    }
                    error = "healthy" !in output.title
                    confidence.value = "${output.confidence.times(100)}%"
                    if(error && !openDialog.value && output.title != "background")
                        openDialog.value = true
                }
            }
        }
        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            width = coordinates.size.width
                            height = coordinates.size.height
                        }
                ) {
                    bitmap1.value?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            null,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        FloatingActionButton(
                            onClick = {
                                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                    val photoFile: File? = try {
                                        createImageFile()
                                    } catch (e: IOException) {
                                        Log.e("idk", e.message.toString())
                                        null
                                    }
                                    photoFile?.also {
                                        val photoURI: Uri = FileProvider.getUriForFile(
                                            context,
                                            "com.sdgwarriors.healthygrove",
                                            it
                                        )
                                        takePictureIntent.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            photoURI
                                        )
                                        runOnUiThread {
                                            launcher.launch(takePictureIntent)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(24.dp),
                            backgroundColor =
                            if (isSystemInDarkTheme())
                                Color(0xFF005300)
                            else
                                Color(0xFF73ff5a),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                "",
                                tint =
                                if (isSystemInDarkTheme())
                                    Color(0xFF73ff5a)
                                else
                                    Color(0xFF005300)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image),
                            "",
                            tint =
                            if (isSystemInDarkTheme())
                                Color(0xFF73ff5a)
                            else
                                Color(0xFF005300),
                            modifier = Modifier
                                .clickable {
                                    runOnUiThread {
                                        launcher1.launch("image/*")
                                    }
                                }
                                .padding(horizontal = 64.dp, vertical = 40.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (title.value != "")
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 16.dp,
                                        bottom = 108.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    )
                                    .clip(RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor =
                                if(error){
                                    if (isSystemInDarkTheme())
                                        Color(0xFF8C1D18)
                                    else
                                        Color(0xFFF9DEDC)
                                }else {
                                    if (isSystemInDarkTheme())
                                        Color(0xFF005300)
                                    else
                                        Color(0xFF73ff5a)
                                },
                                contentColor =
                                if(error){
                                    if (isSystemInDarkTheme())
                                        Color(0xFFF2B8B5)
                                    else
                                        Color(0xFFB3261E)
                                }else {
                                    if (isSystemInDarkTheme())
                                        Color(0xFF73ff5a)
                                    else
                                        Color(0xFF005300)
                                }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Result: ${title.value!!}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.W600,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Accuracy: ${confidence.value!!}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W300,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                    }
                    Column(modifier = Modifier.fillMaxSize(),){
                        TopAppBar(
                            title = { Text(
                                "Plant Disease Detection",
                                fontFamily = MaterialTheme.typography.body1.fontFamily
                            ) },
                            backgroundColor = MaterialTheme.colors.background,
                            elevation = 0.dp,
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, null)
                                }
                            },
                        )
                    }

                    if(bitmap1.value.toString() == "null") {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Text(
                                "Select a picture or take a photo",
                                color = MaterialTheme.colors.onBackground,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }

                    if (openDialog.value) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AlertDialog(
                                onDismissRequest = { openDialog.value = false },
                                title = { },
                                text = { Text(
                                    "The plant is detected to have a disease",
                                    fontFamily = MaterialTheme.typography.body1.fontFamily
                                ) },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            startActivity(Intent(context, BotActivity::class.java)
                                                .putExtra("disease", title.value))
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text(
                                            "Know more about the disease",
                                            modifier = Modifier.padding(8.dp),
                                            color =
                                                if(isSystemInDarkTheme())
                                                    Color(0xFFCCC2DC)
                                                else
                                                    Color(0xFF625B71)
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text(
                                            "Dismiss",
                                            modifier = Modifier.padding(8.dp),
                                            color =
                                            if(isSystemInDarkTheme())
                                                Color(0xFFCCC2DC)
                                            else
                                                Color(0xFFB3261E)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getCapturedImage(): Bitmap {
        val targetW: Int = width!!
        val targetH: Int = height!!

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}