package com.instar.frontend_android.ui.fragments

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentPostBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.adapters.GridSpacingItemDecoration
import com.instar.frontend_android.ui.adapters.ImageAndVideoAdapter
import java.text.FieldPosition

class PostFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>{
    private lateinit var binding: FragmentPostBinding
    private lateinit var imagesAndVideosList: ArrayList<ImageAndVideoInternalMemory>
    private lateinit var imagesAdapter: ImageAndVideoAdapter
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var btnListPost: ImageView
    private var isListPost: Boolean = false
    private var savePosition: Int = 0

    override fun onResume() {
        super.onResume()
        if (imagesAndVideosList.size > 0) {
            loadRecyclerView()
        }
    }

    companion object {
        const val LOADER_ID = 101
        const val VIDEO_LOADER_ID = 102
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPostBinding.inflate(inflater, container, false)
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
        LoaderManager.getInstance(this).initLoader(VIDEO_LOADER_ID, null, this)
        imagesRecyclerView = binding.imageRecyclerView
        btnListPost = binding.btnList
        imagesAndVideosList = ArrayList()
        initView()
        return binding.root
    }

    private fun initView() {
        val layoutManager = GridLayoutManager(context, 4)
        imagesRecyclerView.layoutManager =  layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (1 * scale + 0.5f).toInt()
        imagesRecyclerView.addItemDecoration(GridSpacingItemDecoration(4, spacingInPixels, true))
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        btnListPost.setOnClickListener {
            isListPost = !isListPost
            loadRecyclerView()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Video.Media.DURATION
        )
        val selection = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"
        return CursorLoader(requireContext(), MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, sortOrder)
    }
    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        if (loader.id == LOADER_ID && data != null) {
            val idColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateTakenColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val mediaTypeColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val durationColumn = data.getColumnIndex(MediaStore.Video.Media.DURATION)

            while (data.moveToNext()) {
                val id = data.getLong(idColumn)
                val path = data.getString(dataColumn)
                val dateTaken = data.getLong(dateTakenColumn)
                val mediaType = data.getInt(mediaTypeColumn)
                val duration = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO && durationColumn != -1) {
                    data.getLong(durationColumn)
                } else ""
                val contentUri: Uri = when (mediaType) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    else -> continue
                }
                val uri = ContentUris.withAppendedId(contentUri, id)
                imagesAndVideosList.add(
                    ImageAndVideoInternalMemory(id.toString(), uri.toString(), path, dateTaken.toString(), duration.toString(), mediaType)
                )
            }
            loadRecyclerView()
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    private fun returnFragmentTag(tag: String): String {
        when(tag) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString() -> {
                return "Image"
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString() -> {
                return "Video"
            }
        }
        return ""
    }
    private fun loadRecyclerView() {
        if(isListPost) {
            showListFragment(savePosition, savePosition, false)
        } else {
            val item = imagesAndVideosList[savePosition]
            val fragmentTag = returnFragmentTag(item.type.toString())
            showFragment(item, fragmentTag)
        }
        imagesAdapter = ImageAndVideoAdapter(requireContext(), imagesAndVideosList, isListPost, savePosition)
        imagesRecyclerView.adapter = imagesAdapter
        imagesAdapter.setOnItemClickListener(object: ImageAndVideoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int?) {
                if (position != null) {
                    if(isListPost) {
                        showListFragment(position, position, false)
                    } else {
                        var item = imagesAndVideosList[position]
                        val fragmentTag = returnFragmentTag(item.type.toString())
                        showFragment(item, fragmentTag)
                    }
                    savePosition = position
                }
            }
            override fun onDeleteClick(position: Int?, save: Int) {
                if (position != null) {
                    showListFragment(position, save, true)
                    savePosition = save
                }
            }
        })
    }



    private fun showListFragment(position: Int, savePosition: Int, isDelete: Boolean) {
        var item = imagesAndVideosList[position]
        if(isDelete) {
            val fragmentTag = position.toString()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = fragmentManager.findFragmentByTag(fragmentTag)
            if (fragment != null) {
                fragmentTransaction.remove(fragment)
            }
            fragmentTransaction.commit()
            val item = imagesAndVideosList[savePosition]
            showFragmentItem(item, savePosition.toString())
            return
        }
        val tag = position.toString()
        showFragmentItem(item, tag)
    }
    private fun showFragmentItem(item: ImageAndVideoInternalMemory, fragmentTag: String) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)
        when (fragment) {
            is ImagePostFragment -> fragmentTransaction.show(fragment)
            is VideoPostFragment -> fragmentTransaction.show(fragment)
            else -> {
                fragment = if (item.type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    ImagePostFragment().apply {updateData(item)}
                } else {
                    VideoPostFragment().apply {updateData(item)}
                }
                fragmentTransaction.add(R.id.fragmentContainerView, fragment, fragmentTag)
            }
        }
        for (existingFragment in fragmentManager.fragments) {
            if(existingFragment != fragment) {
                fragmentTransaction.hide(existingFragment)
            }
            else fragmentTransaction.show(existingFragment)
        }
        fragmentTransaction.commit()
    }
    private fun showFragment(item: ImageAndVideoInternalMemory, fragmentTag: String) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)
        when (fragment) {
            is ImagePostFragment -> fragment.apply { updateData(item) }
            is VideoPostFragment -> fragment.apply { updateData(item) }
            else -> {
                fragment = if (item.type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    ImagePostFragment().apply {updateData(item)}
                } else {
                    VideoPostFragment().apply {updateData(item)}
                }
                fragmentTransaction.add(R.id.fragmentContainerView, fragment, fragmentTag)
            }
        }
        for (existingFragment in fragmentManager.fragments) {
            if(existingFragment != fragment) {
                fragmentTransaction.hide(existingFragment)
            }
            else fragmentTransaction.show(existingFragment)
        }
        fragmentTransaction.commit()
    }
}