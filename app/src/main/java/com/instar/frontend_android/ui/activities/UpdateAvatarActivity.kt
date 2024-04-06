package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityAddStoryBinding
import com.instar.frontend_android.databinding.ActivityUpdateAvatarBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.GridSpacingItemDecoration
import com.instar.frontend_android.ui.adapters.ImageAndVideoAdapter
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.ImagePostFragment
import com.instar.frontend_android.ui.fragments.PostFragment
import com.instar.frontend_android.ui.fragments.VideoPostFragment
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import java.io.Serializable


class UpdateAvatarActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var userService: UserService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAndVideoAdapter
    private lateinit var fragmentContainer: View
    private lateinit var btnSave: TextView
    private lateinit var btnBack: ImageView
    private lateinit var imagePostFragment: ImagePostFragment
    private var dataList: MutableList<ImageAndVideoInternalMemory> = mutableListOf()
    private lateinit var btnSelect: TextView // Di chuyển khai báo vào đây
    private var isIntentCalled = false
    private var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_avatar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        LoaderManager.getInstance(this).initLoader(PostFragment.LOADER_ID, null, this)
        initView()
    }
    companion object {
        const val LOADER_ID = 101
    }
    private lateinit var fragment: Fragment;
    @SuppressLint("SuspiciousIndentation")
    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
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

        btnSave.setOnClickListener {
            userService = ServiceBuilder.buildService(UserService::class.java, this);

            val bitmap = imagePostFragment.getBitMapImage("Image")
            val rect = imagePostFragment.getCropRect(this.contentResolver)!!
            val rectString = "${rect.left},${rect.top},${rect.right},${rect.bottom}"
            val image = ImageAndVideo(bitmap, dataList[savePosition].uri, rectString,"", 0)
            val intent = Intent(this@UpdateAvatarActivity, EditProfileActivity::class.java)
            intent.putExtra("image", image)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btnSave.setOnClickListener {
            val newPage = Intent(this@UpdateAvatarActivity, EditProfileActivity::class.java)
            startActivity(newPage)
            finish()
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
        val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"
        return CursorLoader(this, MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, sortOrder)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (loader.id == PostFragment.LOADER_ID && data != null) {
            val idColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateTakenColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val mediaTypeColumn = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (data.moveToNext()) {
                val id = data.getLong(idColumn)
                val path = data.getString(dataColumn)
                val dateTaken = data.getLong(dateTakenColumn)
                val mediaType = data.getInt(mediaTypeColumn)
                val contentUri =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val uri = ContentUris.withAppendedId(contentUri, id)
                dataList.add(ImageAndVideoInternalMemory(id.toString(), uri.toString(), path, dateTaken.toString(), "", mediaType))
            }
            loadRecyclerView()
        }
    }
    private var savePosition: Int = 0
    private fun loadRecyclerView() {
        imagePostFragment =  ImagePostFragment().apply { updateData(dataList[0]) }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, imagePostFragment)
            .commit()
        adapter = ImageAndVideoAdapter(this, dataList, isListPost = false, savePosition = 0)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : ImageAndVideoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                savePosition = position
                imagePostFragment.apply { updateData(dataList[position]) }
            }
            override fun onDeleteClick(position: Int, savePosition: Int) {

            }
        })

    }
}