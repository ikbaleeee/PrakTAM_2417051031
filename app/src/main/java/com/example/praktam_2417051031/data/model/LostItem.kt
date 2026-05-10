package com.example.praktam_2417051031.data.model

import com.google.gson.annotations.SerializedName

data class LostItem(

    @SerializedName("id")
    val id: String,

    @SerializedName("itemName")
    val itemName: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("dateTime")
    val dateTime: String,

    @SerializedName("contact")
    val contact: String,

    @SerializedName("image_url")
    val imageUrl: String
)