package com.instar.frontend_android.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding

class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CommentBottomSheetDialogFragment"
    }
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)
//        val density = resources.displayMetrics.density
//        val dpValue = 500
//        val pxValue = density * dpValue
//        layoutComment.translationY = pxValue
//        initView()
        return binding.root
    }


    private fun initView() {
    }

    override fun onStart() {
        super.onStart()
        var dialog = dialog as BottomSheetDialog?
        var bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        var behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
        behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val desiredHeight = Resources.getSystem().displayMetrics.heightPixels
            if (bottomSheet != null) {
                bottomSheet.layoutParams.height = desiredHeight - 400
                bottomSheet.requestLayout()
            }
        }
        dialog.context.setTheme(R.style.CustomBottomSheetDialog)
        return dialog
    }

}