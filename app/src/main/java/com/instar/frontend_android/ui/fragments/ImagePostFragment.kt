package com.instar.frontend_android.ui.fragments

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageView
import com.instar.frontend_android.databinding.FragmentPostImageBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory

class ImagePostFragment : Fragment() {
    private lateinit var binding: FragmentPostImageBinding
    private lateinit var cropImageView: CropImageView
    private lateinit var newData: ImageAndVideoInternalMemory
    private var cropRect: Rect? = null
    fun updateData(newData: ImageAndVideoInternalMemory) {
        this.newData = newData
        updateImageView(newData.uri)
    }

    private fun updateImageView(imageUri: String) {
        if(this::cropImageView.isInitialized) {
            cropImageView.setImageUriAsync(Uri.parse(imageUri))
            updateCropRect()
        }
    }

    private fun updateCropRect() {
        cropRect?.let {
            val newRect = Rect(it.left, it.top, it.right, it.bottom)
            cropImageView.cropRect = newRect
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {

        } else {

        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPostImageBinding.inflate(inflater, container, false)
        cropImageView = binding.cropImageView
        cropImageView.setImageUriAsync(Uri.parse(newData.uri))
        cropImageView.setFixedAspectRatio(true)
        cropImageView.setOnCropImageCompleteListener { _, result ->
            cropRect = result.cropRect
        }
        cropImageView.setAspectRatio(1, 1)
        return binding.root
    }
}