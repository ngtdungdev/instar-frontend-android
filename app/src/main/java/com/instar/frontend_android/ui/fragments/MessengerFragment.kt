package com.instar.frontend_android.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentMessengerBinding
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.adapters.MessageFriendAdapter
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.OnFragmentClickListener
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessengerFragment : Fragment() {
    private lateinit var binding: FragmentMessengerBinding
    private lateinit var messengerCurrentUsername: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var chatList: MutableList<Chat>
    private lateinit var chatAdapters: MessageFriendAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatsRef: DatabaseReference
    private lateinit var chatService: ChatService
    private lateinit var userService: UserService
    private lateinit var userID: String
    private var listener: OnFragmentClickListener? = null
    private fun fragmentClick(position: Int) {
        listener?.onItemClick(position, "MessengerFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentClickListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMessengerBinding.inflate(inflater, container, false)
        messengerCurrentUsername = binding.messengerCurrentUsername
        chatRecyclerView = binding.recyclerViewChats

        userID = Helpers.getUserId(requireContext()).toString()
        chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        chatService = ChatService(requireContext())
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())
        btnBack = binding.back
        btnBack.setOnClickListener {
            if (listener != null) {
                fragmentClick(1)
            }
        }


        lifecycleScope.launch {
            val user = getUserData(userID)
            messengerCurrentUsername.text = user?.username
        }
        chatList = mutableListOf()
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