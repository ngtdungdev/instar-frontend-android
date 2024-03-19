package com.instar.frontend_android.ui.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentCommentBottomSheetDialogBinding
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.adapters.PostCommentAdapter
import com.instar.frontend_android.ui.adapters.VerticalSpaceItemDecoration

class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CommentBottomSheetDialogFragment"
    }
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentList: MutableList<Comment>
    private lateinit var commentAdapter: PostCommentAdapter
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater)
        commentRecyclerView = binding.recyclerView
        initView()
        return binding.root
    }

    private fun initView() {
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
        return comments
    }

    override fun onStart() {
        super.onStart()
        var dialog = dialog as BottomSheetDialog?
        var bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
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
                bottomSheet.layoutParams.height = desiredHeight
                bottomSheet.requestLayout()
            }
        }
        dialog.context.setTheme(R.style.CustomBottomSheetDialog)
        return dialog
    }

}