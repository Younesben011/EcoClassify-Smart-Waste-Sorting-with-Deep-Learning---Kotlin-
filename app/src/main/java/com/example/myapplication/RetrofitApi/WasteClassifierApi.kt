package com.example.myapplication.RetrofitApi

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WasteClassifierApi {

    @GET("/")
    fun testConnection():Call<TestRespond>

    @POST("/upload")
    fun classifyWaste(@Body body:BodyModel):Call<ClassifierRespond>


}