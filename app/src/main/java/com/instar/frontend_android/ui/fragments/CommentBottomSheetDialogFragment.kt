package com.instar.frontend_android.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration


class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var avatarComment: ImageView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var message: EditText
    private lateinit var commentList: MutableList<CommentWithReply>
    private lateinit var commentAdapter: PostCommentAdapter
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding
    private lateinit var layoutComment: View
    private lateinit var post: Post
    private lateinit var user: User


    private lateinit var commentLayoutParams: ConstraintLayout.LayoutParams
    private var collapsedMargin = 0
    private var buttonHeight = 0
    private var expandedHeight = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CommentBottomSheetDialogFragment = CommentBottomSheetDialogFragment()
        const val TAG = "CommentBottomSheetDialogFragment"
        @JvmStatic
        fun newInstance(): CommentBottomSheetDialogFragment {
            return instance
        }
    }

    fun setPost(post: Post){
        this.post = post
    }

    fun setUserId(user: User){
        this.user = user
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)
        commentRecyclerView = binding.commentRecyclerView
        commentRecyclerView = binding.commentRecyclerView
        textInputLayout = binding.textInputLayout
        message = binding.message
        layoutComment = binding.layoutComment
        avatarComment = binding.avatarComment

        // Load avatar image using Glide
        Glide.with(requireContext())
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(avatarComment)

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
                    true ->  commentLayoutParams.topMargin = (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin + 50).toInt()
                    false ->  commentLayoutParams.topMargin = collapsedMargin + 50
                }
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

        buttonHeight = layoutComment.height.plus(40)
        collapsedMargin = peekHeight - buttonHeight
        commentLayoutParams.topMargin = collapsedMargin + 50
        layoutComment.layoutParams = commentLayoutParams

        val recyclerLayoutParams = commentRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        val k = (buttonHeight - 60) / buttonHeight.toFloat()
        recyclerLayoutParams.bottomMargin = (k * buttonHeight).toInt()
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
                    true
                }
                false -> {false}
            }
        }

    }


    private fun setBottomSheetHeight(bottomSheet: FrameLayout, height: Int) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = height
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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