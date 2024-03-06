package com.instar.frontend_android.ui.fragments

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
    fun updateData(newData: ImageAndVideoInternalMemory) {
        this.newData = newData
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPostImageBinding.inflate(inflater, container, false)
        cropImageView = binding.cropImageView
        cropImageView.setFixedAspectRatio(true)
        cropImageView.setAspectRatio(1, 1)
        return binding.root
    }
}