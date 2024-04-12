package com.instar.frontend_android.ui.activities

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityAddStoryBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.adapters.GridSpacingItemDecoration
import com.instar.frontend_android.ui.adapters.ImageAndVideoAdapter
import com.instar.frontend_android.ui.fragments.PostFragment

class AddStoryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>{
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAndVideoAdapter
    private var dataList: MutableList<ImageAndVideoInternalMemory> = mutableListOf()
    private lateinit var btnSelect: TextView // Di chuyển khai báo vào đây
    private var isIntentCalled = false
    private var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo btnSelect sau khi gọi setContentView()
        btnSelect = findViewById(R.id.btnSelect)

        LoaderManager.getInstance(this).initLoader(PostFragment.LOADER_ID, null, this)
        LoaderManager.getInstance(this).initLoader(PostFragment.VIDEO_LOADER_ID, null, this)
        recyclerView = binding.imageRecyclerView
        initView()
    }
    companion object {
        const val LOADER_ID = 101
        const val VIDEO_LOADER_ID = 102
    }

    private fun initView() {
        adapter = ImageAndVideoAdapter(this, dataList, isListPost = false, savePosition = 0)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager =  layoutManager
        val scale = resources.displayMetrics.density
        val spacingInPixels = (1 * scale + 0.5f).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, spacingInPixels, true))
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        btnSelect.setOnClickListener(View.OnClickListener {
            val selectedItem = dataList[pos].uri
            val intent = Intent(this@AddStoryActivity, EditAddStoryActivity::class.java)
            intent.putExtra("imageUri", selectedItem)
            startActivity(intent)
            isIntentCalled = true
        })
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
        return CursorLoader(this, MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, sortOrder)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        if (loader.id == PostFragment.LOADER_ID && data != null) {
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
                dataList.add(
                    ImageAndVideoInternalMemory(id.toString(), uri.toString(), path, dateTaken.toString(), duration.toString(), mediaType)
                )
            }
            loadRecyclerView()
        }
    }

    private fun loadRecyclerView() {
        adapter = ImageAndVideoAdapter(this, dataList, isListPost = false, savePosition = 0)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : ImageAndVideoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (!isIntentCalled) { // Kiểm tra xem Intent đã được gọi trước đó chưa
                    this@AddStoryActivity.pos = position
                }
            }
            override fun onDeleteClick(position: Int, savePosition: Int) {}
        })
    }

}
