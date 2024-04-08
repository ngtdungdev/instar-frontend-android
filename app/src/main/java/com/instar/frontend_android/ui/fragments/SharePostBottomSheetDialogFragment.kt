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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.databinding.FragmentSharePostBottomSheetDialogBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.SharePostBottomSheetDialogAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private lateinit var userService: UserService
    private lateinit var binding: FragmentSharePostBottomSheetDialogBinding
    private lateinit var userList: MutableList<User>
    private lateinit var shareAdapter: SharePostBottomSheetDialogAdapter
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
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())

        initView()
        loadAdapter()
        return binding.root
    }


    fun initView() {
        textInputLayout.isHintEnabled = false
    }

    private fun loadAdapter() {
        userList = getUserList()
        shareAdapter = SharePostBottomSheetDialogAdapter(requireContext(), userList, lifecycleScope)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        avatarRecyclerView.layoutManager = layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (10 * scale + 0.5f).toInt()
        avatarRecyclerView.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        avatarRecyclerView.adapter = shareAdapter
    }

    private fun getUserList(): MutableList<User> {
        val users: MutableList<User> = mutableListOf()

        lifecycleScope.launch {

        }

        return users
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

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.searchUsers(userId).awaitResponse()
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