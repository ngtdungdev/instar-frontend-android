package com.instar.frontend_android.ui.fragments

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
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration


class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var message: EditText
    private lateinit var commentList: MutableList<Comment>
    private lateinit var commentAdapter: PostCommentAdapter
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding
    private lateinit var layoutComment: View


    private lateinit var commentLayoutParams: ConstraintLayout.LayoutParams
    private var collapsedMargin = 0
    private var buttonHeight = 0
    private var expandedHeight = 0

    companion object {
        private lateinit var instance: CommentBottomSheetDialogFragment
        const val TAG = "CommentBottomSheetDialogFragment"
        @JvmStatic
        fun newInstance(): CommentBottomSheetDialogFragment {
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)
        commentRecyclerView = binding.recyclerView
        textInputLayout = binding.textInputLayout
        message = binding.message
        layoutComment = binding.layoutComment
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
        commentAdapter = PostCommentAdapter(requireContext(), commentList)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        commentRecyclerView.layoutManager = layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (10 * scale + 0.5f).toInt()
        commentRecyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComment(): MutableList<Comment> {
        val comments: MutableList<Comment> = mutableListOf()
        comments.add(Comment("1", "Quang Duy", "Hôm nay trời đẹp không Dũng", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng"), "1", "1"))
        comments.add(Comment("2", "Tiến Dũng", "Hôm nay buồn lắm", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Quang Duy"), "1", "1"))
        comments.add(Comment("3", "Trọng Hiếu", "Tau cũng buồn vì mất điện thoại, tại thằng hưng", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng"), "1", "1"))
        comments.add(Comment("4", "Thành Hưng", "Tại gì t mày", "1", "111", listOf("Quang Duy", "Thái An"), "1", "1"))
        comments.add(Comment("5", "Thái An", "T có bồ rồi bây", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng", "Quang Duy"), "1", "1"))
        comments.add(Comment("1", "Quang Duy", "Hôm nay trời đẹp không Dũng", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng"), "1", "1"))
        comments.add(Comment("2", "Tiến Dũng", "Hôm nay buồn lắm", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Quang Duy"), "1", "1"))
        comments.add(Comment("3", "Trọng Hiếu", "Tau cũng buồn vì mất điện thoại, tại thằng hưng", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng"), "1", "1"))
        comments.add(Comment("4", "Thành Hưng", "Tại gì t mày", "1", "111", listOf("Quang Duy", "Thái An"), "1", "1"))
        comments.add(Comment("5", "Thái An", "T có bồ rồi bây", "1", "111", listOf("Trọng Hiếu", "Thành Hưng", "Tiến Dũng", "Quang Duy"), "1", "1"))
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
//        commentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val inputMethodManager = requireView().context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                if (inputMethodManager.isAcceptingText) {
//                    inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
//                    setBottomSheetHeight(bottomSheet,expandedHeight)
//                }
//            }
//        })
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