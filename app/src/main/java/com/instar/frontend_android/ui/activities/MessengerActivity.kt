package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityMessengerBinding
import com.instar.frontend_android.ui.DTO.Friends
import com.instar.frontend_android.ui.adapters.MessageFriendAdapter

class MessengerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessengerBinding
    private lateinit var messageFriendList: List<Friends>
    private lateinit var newsMessageFriendAdapter: MessageFriendAdapter
    private lateinit var messageFriendsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageFriendsRecyclerView = binding.recyclerViewFriend

        messageFriendList = getFriend()
        newsMessageFriendAdapter = MessageFriendAdapter(messageFriendList)
        messageFriendsRecyclerView.layoutManager = LinearLayoutManager(this)
        messageFriendsRecyclerView.adapter = newsMessageFriendAdapter
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