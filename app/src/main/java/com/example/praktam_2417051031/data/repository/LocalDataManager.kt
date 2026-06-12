package com.example.praktam_2417051031.data.repository

import android.content.Context
import com.example.praktam_2417051031.data.model.LostItem
import com.example.praktam_2417051031.data.model.Comment
import com.example.praktam_2417051031.data.model.ChatMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class LocalDataManager(context: Context) {
    private val gson = Gson()
    private val itemsFile = File(context.filesDir, "local_items_v2.json")
    private val commentsFile = File(context.filesDir, "comments_v2.json")
    private val chatsFile = File(context.filesDir, "chats_v2.json")

    fun getLocalItems(): List<LostItem> {
        if (!itemsFile.exists()) return emptyList()
        return try {
            val json = itemsFile.readText()
            val type = object : TypeToken<List<LostItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveLocalItem(item: LostItem) {
        val currentItems = getLocalItems().toMutableList()
        currentItems.add(item)
        try {
            val json = gson.toJson(currentItems)
            itemsFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getComments(itemId: String): List<Comment> {
        if (!commentsFile.exists()) return emptyList()
        return try {
            val json = commentsFile.readText()
            val type = object : TypeToken<List<Comment>>() {}.type
            val allComments: List<Comment> = gson.fromJson(json, type) ?: emptyList()
            allComments.filter { it.itemId == itemId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveComment(comment: Comment) {
        val allComments = getAllComments().toMutableList()
        allComments.add(comment)
        try {
            val json = gson.toJson(allComments)
            commentsFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAllComments(): List<Comment> {
        if (!commentsFile.exists()) return emptyList()
        return try {
            val json = commentsFile.readText()
            val type = object : TypeToken<List<Comment>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getChatMessages(): List<ChatMessage> {
        if (!chatsFile.exists()) {
            // Prepopulate with a few mock messages so the chat history isn't completely empty
            val initialMessages = listOf(
                ChatMessage("m1", "Budi (Dompet Hitam)", "Me", "Halo, apakah benar Anda menemukan dompet hitam saya?", "08:30", false),
                ChatMessage("m2", "Me", "Budi (Dompet Hitam)", "Iya betul, saya temukan di GKU. Bisa tolong sebutkan isinya untuk konfirmasi?", "08:32", true),
                ChatMessage("m3", "Siti (Kunci Motor)", "Me", "Kunci motor saya yang hilang sudah ketemu belum ya kak?", "Kemarin", false)
            )
            try {
                val json = gson.toJson(initialMessages)
                chatsFile.writeText(json)
                return initialMessages
            } catch (e: Exception) {
                return emptyList()
            }
        }
        return try {
            val json = chatsFile.readText()
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveChatMessage(message: ChatMessage) {
        val allMessages = getChatMessages().toMutableList()
        allMessages.add(message)
        try {
            val json = gson.toJson(allMessages)
            chatsFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
