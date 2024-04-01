package com.instar.frontend_android.ui.activities

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
    }
//    private fun getMediaFromMediaStore(): List<ImageAndVideoInternalMemory> {
//        val mediaList: MutableList<ImageAndVideoInternalMemory> = mutableListOf()
//
//        // Lấy URI của tất cả các hình ảnh và video từ MediaStore
//        val uri: Uri = MediaStore.Files.getContentUri("external")
//
//        // Xác định các cột cần lấy từ cơ sở dữ liệu MediaStore
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.DATA,
//            MediaStore.Files.FileColumns.DATE_TAKEN,
//            MediaStore.Files.FileColumns.MEDIA_TYPE,
//            MediaStore.Video.Media.DURATION
//        )
//
//        // Lọc ra chỉ các hình ảnh và video
//        val selection = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
//        val selectionArgs = arrayOf(
//            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
//            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
//        )
//
//        // Sắp xếp theo ngày chụp giảm dần
//        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"
//
//        // Thực hiện truy vấn và lấy dữ liệu
//        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
//        cursor?.use { cursor ->
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
//                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
//                val dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN))
//                val mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
//                val duration = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
//                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
//                } else 0
//                val mediaUri: Uri = Uri.withAppendedPath(uri, id.toString())
//                mediaList.add(ImageAndVideoInternalMemory(id.toString(), mediaUri.toString(), path, dateTaken.toString(), duration.toString(), mediaType))
//            }
//        }
//
//        return mediaList
//    }
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

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

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

        // Cập nhật RecyclerView sau khi có dữ liệu
//        adapter.setOnItemClickListener()
//        adapter.notifyDataSetChanged()
    }
}
