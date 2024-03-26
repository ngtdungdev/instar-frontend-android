package com.instar.frontend_android.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.databinding.FragmentSharePostBottomSheetDialogBinding
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.adapters.PostCommentAdapter

class SharePostBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CommentBottomSheetDialogFragment = CommentBottomSheetDialogFragment()
        const val TAG = "SharePostBottomSheetDialogFragment"
        @JvmStatic
        fun newInstance(): CommentBottomSheetDialogFragment {
            return instance
        }
    }

    private lateinit var binding: FragmentSharePostBottomSheetDialogBinding
    private lateinit var avatarRecyclerView: RecyclerView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var message: TextInputEditText
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
        layoutBtn = binding.layoutBtn
        initView()
        return binding.root
    }


    fun initView() {
        textInputLayout.isHintEnabled = false
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

//        message.setOnClickListener {
//            message.hint = "Bình luận về ..."
//            val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            if (!inputMethodManager.isAcceptingText) {
//                inputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
//                setBottomSheetPeekHeight(bottomSheet,(expandedHeight))
//            }
//        }
//        message.setOnEditorActionListener { view, actionId, _ ->
//            when(actionId == EditorInfo.IME_ACTION_DONE) {
//                true -> {
//                    val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//                    setBottomSheetPeekHeight(bottomSheet, expandedHeight)
//                    true
//                }
//                false -> {false}
//            }
//        }


//        commentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val inputMethodManager = requireView().context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                if (inputMethodManager.isAcceptingText) {
//                    inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
//                    setBottomSheetPeekHeight(bottomSheet,expandedHeight)
//                }
//            }
//        })
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