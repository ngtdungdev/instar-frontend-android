package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityDirectMessageBinding
import com.instar.frontend_android.ui.DTO.Messages
import com.instar.frontend_android.ui.adapters.DirectMessageAdapter


//mai nộp bài
class DirectMessageActivity : AppCompatActivity(){
    private lateinit var biding: ActivityDirectMessageBinding
    private lateinit var message: EditText
    private lateinit var btnAdd: TextView
    private lateinit var iconAvatar : ImageButton
    private lateinit var iconLibrary: ImageButton
    private lateinit var iconMicro: ImageButton
    private lateinit var messageList: List<Messages>
    private lateinit var messageAdapter: DirectMessageAdapter
    private lateinit var messageRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivityDirectMessageBinding.inflate(layoutInflater)
        setContentView(biding.root)
        messageRecyclerView = biding.recyclerView
        message = biding.message
        btnAdd = biding.btnAdd
        iconAvatar = biding.iconAvatar
        iconLibrary = biding.iconLibrary
        iconMicro = biding.iconMicro
        initView()
    }
    private fun initView() {
        messageList = getMessages()
        messageAdapter = DirectMessageAdapter(messageList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager
        messageRecyclerView.adapter = messageAdapter

        message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!message.text.toString().isEmpty()) {
                    iconMicro.visibility = View.GONE
                    iconLibrary.visibility = View.GONE
                    iconAvatar.visibility = View.GONE
                    btnAdd.visibility = View.VISIBLE
                    message.width = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    iconMicro.visibility = View.VISIBLE
                    iconLibrary.visibility = View.VISIBLE
                    iconAvatar.visibility = View.VISIBLE
                    btnAdd.visibility = View.GONE
                    message.width = 300
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getMessages(): List<Messages> {
        val messages = ArrayList<Messages>()
        val message1 = Messages(Messages.TYPE_AVATAR ,"Bạn ăn cơm không","conmeo")
        val message2 = Messages(Messages.TYPE_RECEIVED_MESSAGE,"Bạn ăn cơm chưa", "conmeo")
        val message3 = Messages(Messages.TYPE_SENT_MESSAGE,"Bạn ăn cơm chưa", "conmeo")
        messages.add(message1)
        messages.add(message2)
        messages.add(message3)
        return messages;
    }

}