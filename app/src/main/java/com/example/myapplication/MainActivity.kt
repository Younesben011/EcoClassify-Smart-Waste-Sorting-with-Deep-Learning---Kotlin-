package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.example.myapplication.RetrofitApi.BodyModel
import com.example.myapplication.RetrofitApi.classifyImage
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Primary700
import com.example.myapplication.ui.theme.Purple500
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class MainActivity : ComponentActivity() {
    val image_uri= mutableStateOf<Uri?>(null)
    val output= mutableStateOf("")
//    val image_uri= mutableStateOf<Uri?>(null)
    val DefaultStroke=Stroke(width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f,20f),0f))
    val stroke= mutableStateOf(DefaultStroke)
    val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()){
        it?.let {
            image_uri.value=it
            stroke.value=Stroke(width =0f)
            val encodedImage=ImageToBase64(it)
            encodedImage?.let {
                        Log.d("respond",it.length.toString())
                lifecycleScope.launch {
                    val respond=classifyImage(BodyModel(encoded_image = it))
                    respond?.let {
                        Log.d("respond",it)
                        output.value=it
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val onImageSelected:(Uri)->Unit

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    PickerScreen()
                }
            }
        }
    }
    @Composable
    fun PickerScreen(){
        val height =300
        Column(modifier=Modifier
            .fillMaxSize()
            .padding(20.dp,0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
//            val stroke=
            Text(
                text="Waste Classifier",
                fontSize= MaterialTheme.typography.h2.fontSize,
                fontWeight = FontWeight.Bold,
                color = Primary700
            )
            Spacer(Modifier.height(40.dp))
            Box(contentAlignment = Alignment.Center,
                modifier =Modifier
                    .fillMaxWidth()
                    .height(height.dp)
                    .drawBehind {
                        drawRoundRect(
                            color= Color.Gray ,
                            style = stroke.value, cornerRadius = CornerRadius(30.dp.toPx())
                        )
                    }){
                    if(image_uri.value==null)
                        ImagePicker()
                    image_uri.value?.let{
                        Image(
                            painter = rememberImagePainter(image_uri.value),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height.dp)
                                .clip(RoundedCornerShape(10.dp)) ,
                            )
                    }


            }
            image_uri.value?.let {
                Row {
                    Text(
                        text = "waste classifier output:",
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = output.value,
                        color = Purple500,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(onClick = {
                    image_uri.value=null
                    stroke.value= DefaultStroke
                    output.value=""
                }){
                    Text(text = "Reset")
                }
            }

        }

    }

    @Composable
    fun ImagePicker() {
        val context = LocalContext.current
        Text(
            text = "Upload your image..",
            color = Color.Gray,
            fontSize = MaterialTheme.typography.h5.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { pickMedia.launch("image/*") },
        )
    }



    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MyApplicationTheme {
        }
    }


    fun ImageToBase64(uri:Uri):String?{
        val inputStream:InputStream?=null
        try {
            val inputStream = contentResolver.openInputStream(uri)
//            val  imageData = FileInputStream(uri.toString())
            val bytes = ByteArrayOutputStream().use {outStream->
                inputStream?.copyTo(outStream)
                outStream.toByteArray()
            }
            val encoded_string = Base64.encodeToString(bytes,Base64.DEFAULT)
            return encoded_string
        }catch (er:Exception){
            Log.e("error",er.message.toString())
        }finally {
            inputStream?.close()
        }

        return null
    }


}




