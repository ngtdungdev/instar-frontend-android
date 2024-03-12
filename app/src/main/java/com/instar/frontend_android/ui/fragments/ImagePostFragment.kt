package com.instar.frontend_android.ui.fragments

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.IOException

class ImagePostFragment : Fragment() {
    private lateinit var binding: FragmentPostImageBinding
    private lateinit var cropImageView: CropImageView
    private lateinit var newData: ImageAndVideoInternalMemory

    fun updateData(newData: ImageAndVideoInternalMemory) {
        this.newData = newData
        if (this::cropImageView.isInitialized) {
            updateImageView(newData.uri)
        }
    }

    fun getCropRect(contentResolver: ContentResolver): Rect? {
        val cropRect = cropImageView.cropRect
        val imageUri = newData.uri
        val imageView = cropImageView

        val imageBitmap = getBitmapFromUri(contentResolver, Uri.parse(imageUri))
        if (imageBitmap != null && cropRect != null) {
            val imageViewWidth = imageView.width
            val imageViewHeight = imageView.height
            val imageWidth = imageBitmap.width
            val imageHeight = imageBitmap.height

            val left = cropRect.left * imageWidth / imageViewWidth
            val top = cropRect.top * imageHeight / imageViewHeight
            val right = cropRect.right * imageWidth / imageViewWidth
            val bottom = cropRect.bottom * imageHeight / imageViewHeight

            return Rect(left, top, right, bottom)
        }
        return null
    }

    private fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            // Use ContentResolver to open an InputStream from Uri
            val inputStream = contentResolver.openInputStream(uri)
            // Read the image from InputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            // Handle error when unable to read the image from Uri
            e.printStackTrace()
            null
        }
    }

    fun getBitMapImage(): Bitmap? {
        return cropImageView.croppedImage
    }

    private fun updateImageView(imageUri: String) {
        cropImageView.setImageUriAsync(Uri.parse(imageUri))
        cropImageView.setFixedAspectRatio(true)
        cropImageView.setAspectRatio(1, 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostImageBinding.inflate(inflater, container, false)
        cropImageView = binding.cropImageView
        if (this::newData.isInitialized) {
            updateImageView(newData.uri)
        }
        return binding.root
    }
}
