package com.instar.frontend_android.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import java.util.Date
import kotlin.math.max



class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var avatarComment: ImageView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var message: TextInputEditText
    private lateinit var commentList: MutableList<CommentWithReply>
    private lateinit var commentAdapter: PostCommentAdapter
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding
    private lateinit var layoutComment: View
    private lateinit var post: Post
    private lateinit var user: User
    private var comment: Comment? = null
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var notificationService: NotificationService

    private lateinit var commentLayoutParams: ConstraintLayout.LayoutParams
    private var collapsedMargin = 0
    private var layoutCommentHeight = 0
    private var expandedHeight = 0
    private var peekHeightFragment = 0

    interface OnCommentChangedListener {
        fun onCommentChanged(commentWithReply: MutableList<CommentWithReply>)
    }

    private var onCommentChangedListener: OnCommentChangedListener? = null

    fun setOnCommentChangedListener(listener: OnCommentChangedListener) {
        onCommentChangedListener = listener
    }

    private fun updateCommentList(commentWithReplies: MutableList<CommentWithReply>) {
        // Cập nhật danh sách comment
        onCommentChangedListener?.onCommentChanged(commentWithReplies)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CommentBottomSheetDialogFragment = CommentBottomSheetDialogFragment()
        const val TAG = "CommentBottomSheetDialogFragment"
        @JvmStatic
        fun newInstance(): CommentBottomSheetDialogFragment {
            return instance
        }

        @JvmStatic
        fun setEditText(context: Context, text: String) {
            val binding = FragmentCommentBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
            binding.message.setText(text)
        }

        @JvmStatic
        fun mention(comment: Comment, user: User) {
            instance.comment = comment

            val mentionText = if (user != null && !user.username.isNullOrBlank()) {
                "@${user.username}"
            } else {
                "@Anonymous"
            }

            // Tạo chuỗi mới mà không chứa dấu ngoặc vuông
            val formattedText = "$mentionText "

            // Tạo một SpannableStringBuilder mới
            val spannable = SpannableStringBuilder(formattedText)

            // Tạo Span cho phần tên người dùng
            val boldSpan = StyleSpan(Typeface.BOLD)
            spannable.setSpan(boldSpan, 0, formattedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Set text và selection cho EditText
            val editable = Editable.Factory.getInstance().newEditable(spannable)
            instance.binding.message.text = editable
            instance.binding.message.setSelection(editable.length)

            // Make EditText clickable
            instance.binding.message.movementMethod = LinkMovementMethod.getInstance()
        }

        @JvmStatic
        fun getEditText(context: Context): EditText {
            val binding = FragmentCommentBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
            return binding.message
        }
    }

    fun setPost(post: Post){
        this.post = post
    }

    fun setUserId(user: User){
        this.user = user
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)
        commentRecyclerView = binding.commentRecyclerView
        textInputLayout = binding.textInputLayout
        message = binding.message
        layoutComment = binding.layoutComment
        avatarComment = binding.avatarComment
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())
        postService = ServiceBuilder.buildService(PostService::class.java, requireContext())

        // Load avatar image using Glide
        Glide.with(requireContext())
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(avatarComment)

        binding.message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { text ->
                    // Check if "@" is typed
                    if (text.length > start && text[start] == '@') {
                        // Show list of users for mentioning
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    val text = editable.toString()

                    // Định nghĩa mẫu cho các cụm văn bản cần xóa
                    val mentionPattern = Regex("^@.*\\\$|@Anonymous")

                    // Tìm kiếm tất cả các mẫu trong văn bản và xóa chúng
                    mentionPattern.findAll(text).forEach { match ->
                        editable.delete(match.range.first, match.range.last + 1) // Xóa từ vị trí đầu đến vị trí cuối của mẫu
                    }
                }
            }
        })

        binding.message.setOnKeyListener { v, keyCode, event ->
            println(event.keyCode)
            if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                notificationService = ServiceBuilder.buildService(NotificationService::class.java, requireContext());
                fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, requireContext());

                val newComment = Comment("", user.username,
                    binding.message.text.toString(),
                    comment?.parentId ?: comment?.id,
                    user.id,
                    mutableListOf(),
                    Date().time.toString(),
                    Date().time.toString())

                postService.commentPost(post.id, newComment).handleResponse(
                    onSuccess = { response ->
                        val comment = response.data?.comment ?: return@handleResponse

                        commentList.add(0, CommentWithReply(comment, mutableListOf()))
                        val clonedList = commentList.toMutableList()
                        commentList.clear()
                        commentList.addAll(clonedList)
                        commentAdapter.notifyItemInserted(0)
                        commentRecyclerView.scrollToPosition(0)
                        commentAdapter.notifyDataSetChanged()

                        instance.comment = null
                        instance.message.clearFocus()
                        instance.message.clearComposingText()
                        instance.message.text?.clear()

                        if (comment.parentId?.isBlank() == true || comment?.parentId?.isEmpty() == true) {
                            val notificationRequest = NotificationRequest(post.id,
                                comment.id, user.id, post.userId, "${user.username} đã bình luận về bài viết của bạn", "add-comment")

                            notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the comment notification.") },
                                onError = { println("Error while sending comment notification.") }
                            )

                            fcmNotificationService.sendAddCommentNotification(notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the add comment notification.") },
                                onError = { println("Error while sending add comment notification.") }
                            )
                        } else {
                            val commentParent = post.comments.find { it.id.equals(comment.parentId) }

                            if (commentParent != null) {
                                val commentWithReplyIdx = commentList.indexOfFirst {
                                    it.comment.id.equals(commentParent.id)
                                }

                                commentList[commentWithReplyIdx].replies.add(0, comment)
                                val clonedList = commentList.toMutableList()
                                commentList.clear()
                                commentList.addAll(clonedList)
                                commentAdapter.notifyItemInserted(0)
                                commentRecyclerView.scrollToPosition(0)
                                commentAdapter.notifyDataSetChanged()

                                val notificationReplyRequest = NotificationRequest(post.id,
                                    commentParent.id, user.id,
                                    commentParent.userId, "${user.username} đã phản hồi bình luận của bạn", "reply-comment")

                                commentAdapter.notifyItemChanged(commentWithReplyIdx)

                                commentParent.userId.let {
                                    notificationService.createNotification(it, notificationReplyRequest).handleResponse(
                                        onSuccess = { println("Successfully sent the reply comment notification.") },
                                        onError = { println("Error while sending reply comment notification.") }
                                    )
                                }

                                fcmNotificationService.sendReplyCommentNotification(notificationReplyRequest).handleResponse(
                                    onSuccess = { println("Successfully sent the reply comment notification.") },
                                    onError = { println("Error while sending reply comment notification.") }
                                )

                                val notificationRequest = NotificationRequest(post.id,
                                    comment.id, user.id, post.userId, "${user.username} đã bình luận về bài viết của bạn", "add-comment")

                                notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                    onSuccess = { println("Successfully sent the comment notification.") },
                                    onError = { println("Error while sending comment notification.") }
                                )

                                fcmNotificationService.sendAddCommentNotification(notificationRequest).handleResponse(
                                    onSuccess = { println("Successfully sent the add comment notification.") },
                                    onError = { println("Error while sending add comment notification.") }
                                )
                            }
                        }


                        updateCommentList(commentList);
                        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
                    },
                    onError = { error ->
                        Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show()
                        instance.comment = null
                        instance.message.clearFocus()
                        instance.message.clearComposingText()
                        instance.message.text?.clear()
                    }
                )

                return@setOnKeyListener true
            }
            return@setOnKeyListener false // Trả về false để tiếp tục xử lý sự kiện
        }



        initView()
        return binding.root
    }

    private fun initView() {
        textInputLayout.isHintEnabled = false
        message.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            when(hasFocus) {
                true -> {
                    message.hint = "Bình luận về ..."
                }
                false -> {
                    message.hint = "Thêm bình luận ..."
                }
            }
        }
        loadAdapter();
    }

    private fun loadAdapter() {
        commentList = getComment()
        commentAdapter = PostCommentAdapter(requireContext(), commentList, lifecycleScope, post.id, user.id)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        commentRecyclerView.layoutManager = layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (10 * scale + 0.5f).toInt()
        commentRecyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComment(): MutableList<CommentWithReply> {
        val comments: MutableList<CommentWithReply> = mutableListOf()

        post.comments.forEach { comment ->
            if (comment.parentId == null) {
                comments.add(CommentWithReply(comment, mutableListOf()))
            }
        }

        post.comments.forEach {comment ->
            if (comment.parentId != null) {
                val parentId = comment.parentId
                val parentCommentIndex = comments.indexOfFirst {
                    it.comment.id != null && it.comment.id == parentId
                }

                if (parentCommentIndex != -1) {
                    comments[parentCommentIndex].replies.add(comment)
                }
            }
        }

        comments.sortByDescending { it.comment.createdAt }
        comments.forEach {
            it.replies.sortByDescending { it.createdAt }
        }

        return comments
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            setupRatio(dialogInterface as BottomSheetDialog)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                commentLayoutParams = layoutComment.layoutParams as ConstraintLayout.LayoutParams
                when(slideOffset >= 0) {
                    true ->  commentLayoutParams.topMargin = (((expandedHeight - layoutCommentHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                    false -> {
                            commentLayoutParams.topMargin = collapsedMargin
//                        commentLayoutParams.topMargin = ((expandedHeight - (expandedHeight * -slideOffset * (10 / 7)).toInt()) + collapsedMargin).toInt()
                    }
                }
                Log.i("com", commentLayoutParams.topMargin.toString())
                layoutComment.layoutParams = commentLayoutParams
            }
        })
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        commentLayoutParams = layoutComment.layoutParams as ConstraintLayout.LayoutParams

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

        layoutCommentHeight = layoutComment.height
        collapsedMargin = peekHeight - layoutCommentHeight
        commentLayoutParams.topMargin = collapsedMargin
        layoutComment.layoutParams = commentLayoutParams

        val recyclerLayoutParams = commentRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        val k = (layoutCommentHeight - 60) / layoutCommentHeight.toFloat()
        recyclerLayoutParams.bottomMargin = (k * layoutCommentHeight).toInt()
        commentRecyclerView.layoutParams = recyclerLayoutParams

        message.setOnClickListener {
            message.hint = "Bình luận về ..."
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

                    notificationService = ServiceBuilder.buildService(NotificationService::class.java, requireContext())
                    fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, requireContext())

                    if (binding.message.text?.isBlank() == true || binding.message.text?.isEmpty() == true) {
                        dismiss()
                    } else {
                        val newComment = Comment("", user.username,
                            binding.message.text.toString(),
                            comment?.parentId ?: comment?.id,
                            user.id,
                            mutableListOf(),
                            Date().time.toString(),
                            Date().time.toString())

                        postService.commentPost(post.id, newComment).handleResponse(
                            onSuccess = { response ->
                                val comment = response.data?.comment ?: return@handleResponse

                                commentList.add(0, CommentWithReply(comment, mutableListOf()))
                                val clonedList = commentList.toMutableList()
                                commentList.clear()
                                commentList.addAll(clonedList)
                                commentAdapter.notifyItemInserted(0)
                                commentRecyclerView.scrollToPosition(0)
                                commentAdapter.notifyDataSetChanged()

                                instance.comment = null
                                instance.message.clearFocus()
                                instance.message.clearComposingText()
                                instance.message.text?.clear()

                                if (comment.parentId?.isBlank() == true || comment?.parentId?.isEmpty() == true) {
                                    val notificationRequest = NotificationRequest(post.id,
                                        comment.id, user.id, post.userId, "${user.username} đã bình luận về bài viết của bạn", "add-comment")

                                    notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                        onSuccess = { println("Successfully sent the comment notification.") },
                                        onError = { println("Error while sending comment notification.") }
                                    )

                                    fcmNotificationService.sendAddCommentNotification(notificationRequest).handleResponse(
                                        onSuccess = { println("Successfully sent the add comment notification.") },
                                        onError = { println("Error while sending add comment notification.") }
                                    )
                                } else {
                                    val commentParent = post.comments.find { it.id.equals(comment.parentId) }

                                    if (commentParent != null) {
                                        val commentWithReplyIdx = commentList.indexOfFirst {
                                            it.comment.id.equals(commentParent.id)
                                        }

                                        commentList[commentWithReplyIdx].replies.add(0, comment)
                                        val clonedList = commentList.toMutableList()
                                        commentList.clear()
                                        commentList.addAll(clonedList)
                                        commentAdapter.notifyItemInserted(0)
                                        commentRecyclerView.scrollToPosition(0)
                                        commentAdapter.notifyDataSetChanged()

                                        val notificationReplyRequest = NotificationRequest(post.id,
                                            commentParent.id, user.id,
                                            commentParent.userId, "${user.username} đã phản hồi bình luận của bạn", "reply-comment")

                                        commentAdapter.notifyItemChanged(commentWithReplyIdx)

                                        commentParent.userId.let {
                                            notificationService.createNotification(it, notificationReplyRequest).handleResponse(
                                                onSuccess = { println("Successfully sent the reply comment notification.") },
                                                onError = { println("Error while sending reply comment notification.") }
                                            )
                                        }

                                        fcmNotificationService.sendReplyCommentNotification(notificationReplyRequest).handleResponse(
                                            onSuccess = { println("Successfully sent the reply comment notification.") },
                                            onError = { println("Error while sending reply comment notification.") }
                                        )

                                        val notificationRequest = NotificationRequest(post.id,
                                            comment.id, user.id, post.userId, "${user.username} đã bình luận về bài viết của bạn", "add-comment")

                                        notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                            onSuccess = { println("Successfully sent the comment notification.") },
                                            onError = { println("Error while sending comment notification.") }
                                        )

                                        fcmNotificationService.sendAddCommentNotification(notificationRequest).handleResponse(
                                            onSuccess = { println("Successfully sent the add comment notification.") },
                                            onError = { println("Error while sending add comment notification.") }
                                        )
                                    }
                                }

                                updateCommentList(commentList);

                                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

                                dismiss() // Đảm bảo rằng dismiss() được gọi trên fragment hiện tại
                            },
                            onError = { error ->
                                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show()
                                instance.comment = null
                                instance.message.clearFocus()
                                instance.message.clearComposingText()
                                instance.message.text?.clear()

                                dismiss() // Đảm bảo rằng dismiss() được gọi trên fragment hiện tại
                            }
                        )
                    }


                    true
                }
                false -> false
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