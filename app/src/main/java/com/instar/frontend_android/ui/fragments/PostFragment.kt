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
import com.instar.frontend_android.ui.adapters.ImageAdapter
class PostFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>{
    private lateinit var binding: FragmentPostBinding
    private lateinit var imagesAndVideosList: ArrayList<ImageAndVideoInternalMemory>
    private lateinit var imagesAdapter: ImageAndVideoAdapter
    private lateinit var imagesRecyclerView: RecyclerView

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
        imagesAndVideosList = ArrayList()
        return binding.root
    }

    private fun showFragment(fragmentTag: String) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            fragment = when(fragmentTag) {
                "ImagePostFragment" -> ImagePostFragment()
                "VideoPostFragment" -> VideoPostFragment()
                else -> throw IllegalArgumentException("Error: $fragmentTag")
            }
            fragmentTransaction.add(R.id.fragmentContainerView, fragment, fragmentTag)
        }
        if (fragment != null) {
            when(fragment) {
                is ImagePostFragment -> fragment.updateData(imagesAndVideosList[0])
                is VideoPostFragment -> fragment.updateData(imagesAndVideosList[0])
            }
        }
        fragmentTransaction.show(fragment).commit()
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
        TODO("Not yet implemented")
    }


    private fun loadRecyclerView() {
        if(imagesAndVideosList[0].type != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            showFragment("VideoPostFragment")
        } else showFragment("ImagePostFragment")
        imagesAdapter = ImageAndVideoAdapter(requireContext(), imagesAndVideosList)
        imagesRecyclerView.adapter = imagesAdapter
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
    }



}