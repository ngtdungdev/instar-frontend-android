package com.instar.frontend_android.ui.activities

import Socket
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.data.remote.sockets.ChatEventListener
import com.instar.frontend_android.data.remote.sockets.ChatStateChangeListener
import com.instar.frontend_android.databinding.ActivityDirectMessageBinding
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.adapters.DirectMessageAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.utils.Helpers

class DirectMessageActivity : AppCompatActivity(), ChatEventListener, ChatStateChangeListener {
    private lateinit var binding: ActivityDirectMessageBinding
    private lateinit var message: EditText
    private lateinit var btnSend: TextView
    private lateinit var iconAvatar : ImageButton
    private lateinit var iconLibrary: ImageButton
    private lateinit var iconMicro: ImageButton
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: DirectMessageAdapter
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var authService: AuthService
    private lateinit var chatService: ChatService
    private lateinit var userID: String
    private lateinit var chatID: String
    private val chatSocket: Socket = Socket.Builder.with("").build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceBuilder.buildService(AuthService::class.java, applicationContext)
        chatService = ServiceBuilder.buildService(ChatService::class.java, applicationContext)
        binding = ActivityDirectMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messageRecyclerView = binding.recyclerView
        message = binding.message
        btnSend = binding.btnSend
        iconAvatar = binding.iconAvatar
        iconLibrary = binding.iconLibrary
        iconMicro = binding.iconMicro

        this.userID = Helpers.getUserId(this) ?: ""
        this.chatID = intent.extras?.getString("chatID") ?: ""
        createConnection()

        initView()
    }

    private fun initView() {
        messageList = getMessages()
        messageAdapter = DirectMessageAdapter(messageList)
        messageAdapter.notifyItemInserted(messageList.size - 1)
        val layoutManager = LinearLayoutManager(this)
        messageRecyclerView.layoutManager = layoutManager
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.scrollToPosition(messageList.size - 1)
        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {

                    loadMoreData()
                }
            }
        })

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            addNewMessage(Message(Message.TYPE_RECEIVED_MESSAGE, ))
//        }, 5000)

        message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (message.text.toString().isEmpty()) {
                    iconMicro.visibility = View.VISIBLE
                    iconLibrary.visibility = View.VISIBLE
                    iconAvatar.visibility = View.VISIBLE
                    btnSend.visibility = View.GONE
                    message.width = 300
                } else {
                    iconMicro.visibility = View.GONE
                    iconLibrary.visibility = View.GONE
                    iconAvatar.visibility = View.GONE
                    btnSend.visibility = View.VISIBLE
                    message.width = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnSend.setOnClickListener {
            val messageText = message.text.toString()
            sendMessage(messageText)
            message.text.clear();
        }
    }

    private fun addNewMessage(newMessage: Message) {
        messageList.add(newMessage)
        messageAdapter.notifyItemInserted(messageList.size - 1)
        messageRecyclerView.scrollToPosition(messageList.size - 1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun loadMoreData() {

    }

    private fun createConnection() {

    }

    private fun sendMessage(messageText: String) {
        addNewMessage(Message(Message.TYPE_SENT_MESSAGE, chatID, userID, messageText))
        // call api from spring boot here
    }

    private fun getMessages(): MutableList<Message> {
        val messages = ArrayList<Message>()
        val message1 = Message(Message.TYPE_AVATAR, chatID, "", "concho")
        val message2 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message3 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message4 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message5 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message6 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message7 = Message(Message.TYPE_SENT_MESSAGE, chatID, "", "concho",)
        val message8 = Message(Message.TYPE_RECEIVED_MESSAGE, chatID, "", "Alo")
        val message9 = Message(Message.TYPE_RECEIVED_MESSAGE, chatID, "", "Alo")
        val message10 = Message(Message.TYPE_RECEIVED_MESSAGE, chatID, "", "Alo")
        val message11 = Message(Message.TYPE_RECEIVED_MESSAGE, chatID, "", "Alo")
        messages.add(message1)
        messages.add(message2)
        messages.add(message3)
        messages.add(message4)
        messages.add(message5)
        messages.add(message6)
        messages.add(message7)
        messages.add(message8)
        messages.add(message9)
        messages.add(message10)
        messages.add(message11)
        return messages;
    }

    override fun onDestroy() {
        super.onDestroy()
//        socketClient.close()
    }

    override fun onNewMessage(message: String) {
        addNewMessage(Message())
    }

    override fun onOpen() {
        Log.d("ChatSocket", "Connection opened")
    }

    override fun onClosed(code: Int, reason: String) {
        Log.d("ChatSocket", "Connection closed: $code - $reason")
    }

    override fun onStateChange(state: Socket.State) {
        TODO("Not yet implemented")
    }
}