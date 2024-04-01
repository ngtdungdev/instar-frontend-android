package com.instar.frontend_android.ui.activities

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityAddStoryBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.adapters.ImageAndVideoAdapter

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAndVideoAdapter
    private var dataList: MutableList<ImageAndVideoInternalMemory> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.imageRecyclerView
        adapter = ImageAndVideoAdapter(this, dataList, isListPost = false, savePosition = 0)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Lấy danh sách hình ảnh và video từ MediaStore
        val mediaList = getMediaFromMediaStore()
        dataList.addAll(mediaList)

        // Cập nhật RecyclerView sau khi có dữ liệu
        adapter.notifyDataSetChanged()
    }

    private fun getMediaFromMediaStore(): List<ImageAndVideoInternalMemory> {
        val mediaList: MutableList<ImageAndVideoInternalMemory> = mutableListOf()

        // Lấy URI của tất cả các hình ảnh và video từ MediaStore
        val uri: Uri = MediaStore.Files.getContentUri("external")

        // Xác định các cột cần lấy từ cơ sở dữ liệu MediaStore
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Video.Media.DURATION
        )

        // Lọc ra chỉ các hình ảnh và video
        val selection = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        // Sắp xếp theo ngày chụp giảm dần
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"

        // Thực hiện truy vấn và lấy dữ liệu
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                val dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN))
                val mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
                val duration = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                } else 0
                val mediaUri: Uri = Uri.withAppendedPath(uri, id.toString())
                mediaList.add(ImageAndVideoInternalMemory(id.toString(), mediaUri.toString(), path, dateTaken.toString(), duration.toString(), mediaType))
            }
        }

        return mediaList
    }
}
