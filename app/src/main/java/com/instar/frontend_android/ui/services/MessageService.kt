package com.instar.frontend_android.ui.services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instar.frontend_android.ui.DTO.Message

class MessageService {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")

    fun createNewMessage(message: Message) {
        messagesRef.push().setValue(message)
            .addOnSuccessListener { println("Message created successfully!") }
            .addOnFailureListener { exception ->
                println("Failed to create message: $exception")
            }
    }

    fun getMessagesByChatId(chatId: String, listener: (List<Message>) -> Unit) {
        messagesRef.orderByChild("chatId").equalTo(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    if (snapshot.exists()) {
                        for (messageSnapshot in snapshot.children) {
                            val message = messageSnapshot.getValue(Message::class.java) ?: continue
                            messages.add(message)
                        }
                    }
                    listener(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error getting message: $error")
                    listener(emptyList()) // Handle errors by passing null
                }
            })
    }

    fun getLastMessageOfChat(chatId: String, listener: (Message?) -> Unit) {
        messagesRef.orderByChild("chatId").equalTo(chatId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var message: Message? = null
                        for (messageSnapshot in snapshot.children) {
                            message = messageSnapshot.getValue(Message::class.java) ?: continue
                        }
                        listener(message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error getting message: $error")
                    listener(null) // Handle errors by passing null
                }
            })
    }
}

