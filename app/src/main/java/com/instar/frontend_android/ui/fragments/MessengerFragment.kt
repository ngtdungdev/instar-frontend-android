package com.instar.frontend_android.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentMessengerBinding
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.adapters.MessageFriendAdapter

class MessengerFragment : Fragment() {
    private lateinit var binding: FragmentMessengerBinding
    private lateinit var messageFriendList: List<Chat>
    private lateinit var newsMessageFriendAdapter: MessageFriendAdapter
    private lateinit var messageFriendsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMessengerBinding.inflate(inflater, container, false)
        messageFriendsRecyclerView = binding.recyclerViewFriend

        messageFriendList = getMembers()
        newsMessageFriendAdapter = MessageFriendAdapter(messageFriendList)
        newsMessageFriendAdapter.setOnItemClickListener(object : MessageFriendAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(context, DirectMessageActivity::class.java)
                intent.putExtra("chatID", messageFriendList[position].id)
                context?.startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        messageFriendsRecyclerView.layoutManager = LinearLayoutManager(context)
        messageFriendsRecyclerView.adapter = newsMessageFriendAdapter
        return binding.root
    }

    private fun getMembers(): ArrayList<Chat> {
        val friendList = ArrayList<Chat>()
        val friend1 = Chat("", listOf("", ""))
        val friend2 = Chat("", listOf("", ""))
        friendList.add(friend1)
        friendList.add(friend2)
        return friendList
    }
}