package com.instar.frontend_android.ui.fragments

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.FragmentPostBinding
import com.instar.frontend_android.ui.DTO.ImageInternalMemory
import com.instar.frontend_android.ui.adapters.ImageAdapter

class PostFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>{
    private lateinit var binding: FragmentPostBinding
    private lateinit var imagesList: ArrayList<ImageInternalMemory>
    private lateinit var imagesAdapter: ImageAdapter
    private lateinit var imagesRecyclerView: RecyclerView
    companion object {
        const val LOADER_ID = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        val currentPosition = data!!.position
        if (currentPosition < 0) {
            imagesList = ArrayList<ImageInternalMemory>()
            if (loader.id == LOADER_ID && data != null) {
//                imagesList.setVisibility(View.GONE)
                val columnIndexId = data.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val columnIndexData = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val columnIndexDateTaken =
                    data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                while (data.moveToNext()) {
                    val id = data.getLong(columnIndexId)
                    val imagePath = data.getString(columnIndexData)
                    val dateTaken = data.getLong(columnIndexDateTaken)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imagesList.add(
                        ImageInternalMemory(
                            id.toString(),
                            imageUri.toString(),
                            imagePath,
                            dateTaken.toString()
                        )
                    )
                }
                loadRecyclerView()
            }
        }
    }

    private fun loadRecyclerView() {
        imagesAdapter = ImageAdapter(requireContext(), imagesList)
        imagesRecyclerView.adapter = imagesAdapter
        val layoutManager = GridLayoutManager(context, 3)
        imagesRecyclerView.layoutManager =  layoutManager
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN)
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        return CursorLoader(requireContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }

}