package com.example.myapplication.RetrofitApi

import android.util.Log
import com.example.myapplication.RetrofitInstance
import retrofit2.await


var retroApi = RetrofitInstance.getInstance().create(WasteClassifierApi::class.java)

suspend fun testConnectivity(){
    val respond = retroApi.testConnection().await()
    respond?.let {
        Log.d("respond",respond.message)
    }
}

suspend fun classifyImage(body: BodyModel):String?{
    val respond = retroApi.classifyWaste(body).await()
    respond?.let {
        Log.d("respond",respond.status)
    }
    return respond.status
}