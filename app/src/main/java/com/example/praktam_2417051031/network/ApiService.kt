package com.example.praktam_2417051031.network

import Model.LostItem
import retrofit2.http.GET

interface ApiService {

    @GET("lost_items.json")
    suspend fun getItems(): List<LostItem>
}