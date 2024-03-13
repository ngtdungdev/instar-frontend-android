package com.instar.frontend_android.ui.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        // chỉnh chiều cao của fragment
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val desiredHeight = Resources.getSystem().displayMetrics.heightPixels - 400
            if (bottomSheet != null) {
                bottomSheet.layoutParams.height = desiredHeight
                bottomSheet.requestLayout()
            }
        }
        // boderlayout
        dialog.context.setTheme(R.style.CustomBottomSheetDialog)
        return dialog
    }
}