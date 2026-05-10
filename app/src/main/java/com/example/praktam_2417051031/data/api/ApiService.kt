package com.example.praktam_2417051031.data.api

import com.example.praktam_2417051031.data.model.LostItem
import retrofit2.http.GET

interface ApiService {

    @GET("lost_items.json")
    suspend fun getItems(): List<LostItem>
}