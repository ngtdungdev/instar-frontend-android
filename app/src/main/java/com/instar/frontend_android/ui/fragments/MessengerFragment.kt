package com.instar.frontend_android.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentMessengerBinding
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.adapters.MessageFriendAdapter
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class MessengerFragment : Fragment() {
    private lateinit var binding: FragmentMessengerBinding
    private lateinit var messengerCurrentUsername: TextView
    private lateinit var chatList: MutableList<Chat>
    private lateinit var chatAdapters: MessageFriendAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatsRef: DatabaseReference
    private lateinit var chatService: ChatService
    private lateinit var userService: UserService
    private lateinit var userID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMessengerBinding.inflate(inflater, container, false)
        messengerCurrentUsername = binding.messengerCurrentUsername
        chatRecyclerView = binding.recyclerViewChats

        userID = Helpers.getUserId(requireContext()).toString()
        chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        chatService = ChatService(requireContext())
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())

        lifecycleScope.launch {
            val user = getUserData(userID)
            messengerCurrentUsername.text = user?.username
        }
        chatList = mutableListOf()
//        val chat = Chat(listOf("65edb2d49c029347d59adda2", "649952f071c109592a41476e").sorted(), LocalDateTime.of(2023, 6, 26, 16, 43, 40, 35000000))
//        chatService.createNewChat(chat)
        chatService.getChatsByUserId(userID) { chats ->
            run {
                chatList.clear()
                chatList.addAll(chats.toMutableList())
                chatAdapters.notifyItemInserted(0)
                chatRecyclerView.scrollToPosition(0)
            }
        }
        chatAdapters = MessageFriendAdapter(requireContext(), lifecycleScope, chatList)
        chatAdapters.setOnItemClickListener(object : MessageFriendAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(context, DirectMessageActivity::class.java)
                intent.putExtra("chatID", chatList[position].members.joinToString("-"))
                requireContext().startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        chatRecyclerView.adapter = chatAdapters
        return binding.root
    }

    private suspend fun getUserData(userId: String): User? {
        val response = withContext(Dispatchers.IO) {
            return@withContext userService.getUser(userId).awaitResponse()
        }
        return response.data?.user
    }
}