package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.databinding.ActivityCommentBottomSheetBinding

class CommentBottomSheetDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBottomSheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}