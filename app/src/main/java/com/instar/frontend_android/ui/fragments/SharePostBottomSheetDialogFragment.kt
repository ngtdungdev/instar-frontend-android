package com.instar.frontend_android.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.databinding.FragmentSharePostBottomSheetDialogBinding
import com.instar.frontend_android.types.requests.MessageRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.SelectedUser
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.SharePostBottomSheetDialogAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.MessageService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharePostBottomSheetDialogFragment(private val post: Post) : BottomSheetDialogFragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CommentBottomSheetDialogFragment = CommentBottomSheetDialogFragment()
        const val TAG = "SharePostBottomSheetDialogFragment"
        @JvmStatic
        fun newInstance(): CommentBottomSheetDialogFragment {
            return instance
        }
    }

    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
    private lateinit var messageService: MessageService
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var binding: FragmentSharePostBottomSheetDialogBinding
    private lateinit var userList: MutableList<SelectedUser>
    private lateinit var shareAdapter: SharePostBottomSheetDialogAdapter
    private lateinit var avatarRecyclerView: RecyclerView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var message: TextInputEditText
    private lateinit var search: EditText
    private lateinit var button: TextView
    private lateinit var layoutBtn: View
    private lateinit var shareLayoutParams: ConstraintLayout.LayoutParams
    private var collapsedMargin = 0
    private var layoutShareHeight = 0
    private var expandedHeight = 0
    private var peekHeightFragment = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSharePostBottomSheetDialogBinding.inflate(layoutInflater)
        avatarRecyclerView = binding.avatarRecyclerView
        textInputLayout = binding.textInputLayout
        message = binding.message
        search = binding.search
        layoutBtn = binding.layoutBtn
        button = binding.button
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())
        chatService = ChatService(requireContext())
        messageService = MessageService()
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, requireContext())
        avatarRecyclerView.layoutManager = LinearLayoutManager(context)

        initView()
        search()

        val debounceHandler = Handler(Looper.getMainLooper())
        var debounceRunnable: Runnable? = null
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let {
                    debounceHandler.removeCallbacks(it)
                }
                debounceRunnable = Runnable {
                    search()
                }
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return binding.root
    }

    fun initView() {
        textInputLayout.isHintEnabled = false
    }

    private fun loadAdapter() {
        shareAdapter = SharePostBottomSheetDialogAdapter(requireContext(), userList, lifecycleScope)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        avatarRecyclerView.layoutManager = layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (10 * scale + 0.5f).toInt()
        avatarRecyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        avatarRecyclerView.adapter = shareAdapter
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            setupRatio(dialogInterface as BottomSheetDialog)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                shareLayoutParams = layoutBtn.layoutParams as ConstraintLayout.LayoutParams
                when(slideOffset >= 0) {
                    true ->  shareLayoutParams.topMargin = (((expandedHeight - layoutShareHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                    false -> {
                        shareLayoutParams.topMargin = collapsedMargin
//                        shareLayoutParams.topMargin = ((expandedHeight - (expandedHeight * -slideOffset * (10 / 7)).toInt()) + collapsedMargin).toInt()
                    }
                }
                Log.i("com", shareLayoutParams.topMargin.toString())
                layoutBtn.layoutParams = shareLayoutParams
            }
        })
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        shareLayoutParams = layoutBtn.layoutParams as ConstraintLayout.LayoutParams

        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        val bottomSheetLayoutParams = bottomSheet.layoutParams
        bottomSheetLayoutParams.height = getBottomSheetDialogDefaultHeight()
        expandedHeight = bottomSheetLayoutParams.height
        val peekHeight = (expandedHeight * 0.6).toInt()

        bottomSheet.layoutParams = bottomSheetLayoutParams
        BottomSheetBehavior.from(bottomSheet).apply {
            skipCollapsed = false
            setPeekHeight(peekHeight)
            isHideable = true
        }

        peekHeightFragment = peekHeight

        layoutShareHeight = layoutBtn.height
        collapsedMargin = peekHeight - layoutShareHeight
        shareLayoutParams.topMargin = collapsedMargin
        layoutBtn.layoutParams = shareLayoutParams

        val recyclerLayoutParams = avatarRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        val k = (layoutShareHeight - 60) / layoutShareHeight.toFloat()
        recyclerLayoutParams.bottomMargin = (k * layoutShareHeight).toInt()
        avatarRecyclerView.layoutParams = recyclerLayoutParams

        button.setOnClickListener {
            for (user in userList) {
                if (user.selected) {
                    var content: String = post.id
                    val senderID: String = Helpers.getUserId(requireContext()).toString()
                    val userID: String = user.user?.id.toString()
                    val chat = Chat(arrayOf(senderID, userID).sorted())
                    val chatID: String = chat.members.joinToString("-")
                    // send the post message
                    val postMessage = Message(content, senderID, chatID, "post")
                    if (!chatService.chatExists(chat.members))
                        chatService.createNewChat(chat)
                    messageService.createNewMessage(postMessage) // push to Firebase
                    var messageRequest = MessageRequest().apply {
                        text = content
                        senderId = senderID
                        chatId = chatID
                    }
                    fcmNotificationService.sendChatNotification(messageRequest).handleResponse(
                        onSuccess = { println("Successfully sent the chat notification.") },
                        onError = { println("Error while sending chat notification.") }
                    )

                    // send the text message
                    if (!message.text.isNullOrBlank()) {
                        content = message.text.toString()
                        val textMessage = Message(content, senderID, chatID)
                        messageService.createNewMessage(textMessage) // push to Firebase
                        messageRequest = MessageRequest().apply {
                            text = content
                            senderId = senderID
                            chatId = chatID
                        }
                        fcmNotificationService.sendChatNotification(messageRequest).handleResponse(
                            onSuccess = { println("Successfully sent the chat notification.") },
                            onError = { println("Error while sending chat notification.") }
                        )
                    }
                }
            }
            // turn off this bottom sheet fragment
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        message.setOnClickListener {
            message.hint = "Soạn tin nhắn..."
            val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!inputMethodManager.isAcceptingText) {
                inputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
                setBottomSheetHeight(bottomSheet,(expandedHeight * 0.6).toInt())
            }
        }
        message.setOnEditorActionListener { view, actionId, _ ->
            when(actionId == EditorInfo.IME_ACTION_DONE) {
                true -> {
                    val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    setBottomSheetHeight(bottomSheet, expandedHeight)
                    true
                }
                false -> {false}
            }
        }
    }

    fun search() {
        lifecycleScope.launch {
            val response = try {
                userService.searchUsers(search.text.toString()).awaitResponse()
            } catch (error: Throwable) {
                error.printStackTrace()
                null
            }

            withContext(Dispatchers.Main) {
                if (response != null) {
                    val users = response.data?.users
                    userList = mutableListOf();
                    users?.forEach {
                        userList.add(SelectedUser(it, false))
                    }
                    loadAdapter()
                } else {
                    Log.e("Error", "Failed to get following users")
                }
            }
        }
    }

    private fun setBottomSheetPeekHeight(bottomSheet: FrameLayout, height: Int) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = height
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setBottomSheetHeight(bottomSheet: FrameLayout, height: Int) {
        val newLayoutParams = bottomSheet.layoutParams
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        newLayoutParams.height = 1000
        bottomSheet.layoutParams = newLayoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight()
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (requireContext() as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}