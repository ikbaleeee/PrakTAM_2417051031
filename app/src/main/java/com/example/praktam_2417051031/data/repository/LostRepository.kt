package com.example.praktam_2417051031.data.repository

import android.content.Context
import com.example.praktam_2417051031.ReportType
import com.example.praktam_2417051031.data.api.RetrofitClient
import com.example.praktam_2417051031.data.model.LostItem
import com.example.praktam_2417051031.data.model.Comment
import com.example.praktam_2417051031.data.model.ChatMessage
import com.example.praktam_2417051031.data.model.ChatRoom

class LostRepository(private val context: Context) {
    private val localDataManager = LocalDataManager(context)

    suspend fun getLostItems(): List<LostItem> {
        val remoteItems = try {
            RetrofitClient.instance.getItems()
        } catch (e: Exception) {
            emptyList()
        }

        val mappedRemote = remoteItems.map { item ->
            val inferredType = if (
                item.description.contains("ditemukan", ignoreCase = true) ||
                item.itemName.contains("ditemukan", ignoreCase = true) ||
                item.description.contains("temu", ignoreCase = true)
            ) {
                ReportType.FOUND
            } else {
                ReportType.LOST
            }
            item.copy(type = item.type ?: inferredType)
        }

        val localItems = localDataManager.getLocalItems()
        val allItems = mappedRemote + localItems

        // Set isResolved dynamically if it has been marked as resolved locally
        return allItems.map { item ->
            item.copy(isResolved = item.isResolved || isItemResolved(item.id))
        }
    }

    fun addLostItem(item: LostItem) {
        localDataManager.saveLocalItem(item)
    }

    fun getComments(itemId: String): List<Comment> {
        return localDataManager.getComments(itemId)
    }

    fun addComment(comment: Comment) {
        localDataManager.saveComment(comment)
    }

    fun getChatMessages(): List<ChatMessage> {
        return localDataManager.getChatMessages()
    }

    fun addChatMessage(message: ChatMessage) {
        localDataManager.saveChatMessage(message)
    }

    fun getChatRooms(): List<ChatRoom> {
        val messages = getChatMessages()
        val currentUser = getSession() ?: "Me"
        val rooms = mutableMapOf<String, ChatMessage>()
        for (msg in messages) {
            val contact = if (msg.senderName == currentUser) msg.receiverName else msg.senderName
            val existing = rooms[contact]
            if (existing == null) {
                rooms[contact] = msg
            } else {
                rooms[contact] = msg
            }
        }
        return rooms.map { (contact, msg) ->
            ChatRoom(
                contactName = contact,
                lastMessage = msg.content,
                timestamp = msg.timestamp
            )
        }
    }

    // Session Management
    fun saveSession(username: String) {
        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.edit().putString("username", username).apply()
    }

    fun getSession(): String? {
        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return prefs.getString("username", null)
    }

    fun clearSession() {
        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    // User Registration & Verification
    fun registerUser(username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        if (prefs.contains(username)) return false // Username already registered
        prefs.edit().putString(username, password).apply()
        return true
    }

    fun validateUser(username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        return prefs.getString(username, null) == password
    }

    // Favorites Management
    fun toggleFavorite(itemId: String) {
        val currentUser = getSession() ?: return
        val prefs = context.getSharedPreferences("user_favorites", Context.MODE_PRIVATE)
        val currentFavs = prefs.getStringSet(currentUser, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (currentFavs.contains(itemId)) {
            currentFavs.remove(itemId)
        } else {
            currentFavs.add(itemId)
        }
        prefs.edit().putStringSet(currentUser, currentFavs).apply()
    }

    fun isFavorite(itemId: String): Boolean {
        val currentUser = getSession() ?: return false
        val prefs = context.getSharedPreferences("user_favorites", Context.MODE_PRIVATE)
        val currentFavs = prefs.getStringSet(currentUser, emptySet()) ?: emptySet()
        return currentFavs.contains(itemId)
    }

    // Status Resolution
    fun resolveItem(itemId: String) {
        val prefs = context.getSharedPreferences("resolved_items", Context.MODE_PRIVATE)
        val resolvedSet = prefs.getStringSet("ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        resolvedSet.add(itemId)
        prefs.edit().putStringSet("ids", resolvedSet).apply()
    }

    private fun isItemResolved(itemId: String): Boolean {
        val prefs = context.getSharedPreferences("resolved_items", Context.MODE_PRIVATE)
        val resolvedSet = prefs.getStringSet("ids", emptySet()) ?: emptySet()
        return resolvedSet.contains(itemId)
    }
}