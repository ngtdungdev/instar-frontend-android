package com.instar.frontend_android.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.FragmentMessengerBinding
import com.instar.frontend_android.ui.DTO.Friends
import com.instar.frontend_android.ui.adapters.MessageFriendAdapter

class MessengerFragment : Fragment() {
    private lateinit var binding: FragmentMessengerBinding
    private lateinit var messageFriendList: List<Friends>
    private lateinit var newsMessageFriendAdapter: MessageFriendAdapter
    private lateinit var messageFriendsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMessengerBinding.inflate(inflater, container, false)
        messageFriendsRecyclerView = binding.recyclerViewFriend

        messageFriendList = getFriend()
        newsMessageFriendAdapter = MessageFriendAdapter(messageFriendList)
        messageFriendsRecyclerView.layoutManager = LinearLayoutManager(context)
        messageFriendsRecyclerView.adapter = newsMessageFriendAdapter
        return binding.root
    }

    private fun getFriend(): ArrayList<Friends> {
        val friendList = ArrayList<Friends>()
        val friend1 = Friends("Tin của bạn","conmeo")
        val friend2 = Friends("Duy ko rep", "conmeo")
        friendList.add(friend1)
        friendList.add(friend2)
        return friendList
    }
}