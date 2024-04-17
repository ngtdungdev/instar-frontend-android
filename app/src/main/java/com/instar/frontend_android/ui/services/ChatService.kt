package com.instar.frontend_android.ui.services

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instar.frontend_android.ui.DTO.Chat

class ChatService(val applicationContext: Context) {
    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.getReference("chats")

    fun createNewChat(chat: Chat) {
        val chatId = chat.members.joinToString("-")
        chatsRef.child(chatId).setValue(chat)
            .addOnSuccessListener { println("Chat created successfully!") }
            .addOnFailureListener { exception ->
                println("Failed to create chat: $exception")
            }
    }

    fun getChatsByUserId(userId: String, listener: (List<Chat>) -> Unit) {
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chats = mutableListOf<Chat>()
                if (dataSnapshot.exists()) {
                    for (chatSnapshot in dataSnapshot.children) {
                        val chat = chatSnapshot.getValue(Chat::class.java) ?: continue
                        if (chatSnapshot.key!!.contains(userId))
                            chats.add(0, chat)
                    }
                }
                listener(chats)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error getting chats: $databaseError")
                listener(emptyList()) // Handle errors by passing an empty list
            }
        })
    }

    fun getChatByMembers(members: List<String>, listener: (Chat?) -> Unit) {
        val sortedMembers = members.sorted()
        val chatId = sortedMembers.joinToString("-")
        if (chatId.isEmpty()) {
            listener(null) // Indicate no chat found with the provided members
        }
        chatsRef.child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    listener(chat)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error getting chat: $databaseError")
                    listener(null) // Handle errors by passing null
                }
            })
    }

    fun chatExists(members: List<String>): Boolean {
        var exists = false
        getChatByMembers(members) { chat: Chat? ->
            run {
                if (chat == null)
                    exists = false
                else if (chat.members.intersect(members.toSet()).size == members.size)
                    exists = true
            }
        }
        return exists
    }
}

