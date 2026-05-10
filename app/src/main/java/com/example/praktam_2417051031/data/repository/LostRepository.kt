package com.example.praktam_2417051031.data.repository
import com.example.praktam_2417051031.data.api.RetrofitClient
import com.example.praktam_2417051031.data.model.LostItem
class LostRepository {
    suspend fun getLostItems(): List<LostItem> {
        return try {
            RetrofitClient.instance.getItems()
        } catch (e: Exception) {
            emptyList()
        }
    }
}