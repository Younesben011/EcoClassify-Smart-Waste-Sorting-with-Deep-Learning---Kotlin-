package com.example.myapplication


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val url= "http://192.168.56.1:3001"
    fun getInstance(): Retrofit{
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}