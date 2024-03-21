package com.instar.frontend_android.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityPostPreupLoadingBinding
import com.instar.frontend_android.types.requests.PostRequest
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.HorizontalSpaceItemDecoration
import com.instar.frontend_android.ui.adapters.SelectedImageAdapter
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class PostPreUpLoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostPreupLoadingBinding
    private lateinit var preUpLoadingAdapter: SelectedImageAdapter
    private var imageAndVideo: MutableList<ImageAndVideo> = mutableListOf()
    private lateinit var preUpLoadingRecyclerView: RecyclerView
    private lateinit var btnTagOther : View
    private lateinit var tagList: MutableList<User>
    private lateinit var btnCreate : View
    private lateinit var contentText : TextView
    private lateinit var imageBack: ImageView
    private lateinit var postService: PostService
    private val TAG_OTHER_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostPreupLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageBack = binding.imageBack
        preUpLoadingRecyclerView = binding.recyclerView
        btnCreate = binding.btnCreate
        btnTagOther = binding.btnTagOthers
        contentText = binding.content
        tagList = mutableListOf()
        imageAndVideo = (intent.getSerializableExtra("Data") as? MutableList<ImageAndVideo>)!!
        postService = ServiceBuilder.buildService(PostService::class.java, applicationContext)
        initView()
    }

    private fun initView() {
        btnCreate.setOnClickListener {
            val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

            val accessToken = sharedPreferences.getString("accessToken", null)

            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")
                val tagUserList = tagList.map { it.id }
                val post = PostRequest(id, contentText.text.toString(), "", tagUserList)

                lifecycleScope.launch {
                    val response = try {
                        val response = postService.createPost(post, Helpers.convertToMultipartParts(imageAndVideo)).awaitResponse()
                        response
                    } catch (error: Throwable) {
                        // Handle error
                        error.printStackTrace() // Print stack trace for debugging purposes
                        null // Return null to indicate that an error occurred
                    }

                    if (response != null && response.status == "200") {
                        Toast.makeText(applicationContext, "Post created", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        // Handle the case where the response is null
                        Log.e("Error", "Failed to get timeline posts")
                    }
                }
            }
        }

        imageBack.setOnClickListener {
            finish()
        }
        btnTagOther.setOnClickListener {
            val intent = Intent(this@PostPreUpLoadingActivity, TagOtherActivity::class.java)
            intent.putExtra("userList", ArrayList(tagList)) // Truyền userList qua Intent
            startActivityForResult(intent, TAG_OTHER_REQUEST_CODE)
        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        preUpLoadingAdapter = SelectedImageAdapter(this, imageAndVideo, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(preUpLoadingRecyclerView)
        preUpLoadingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                snapView?.let {
                    val snapPosition = recyclerView.getChildAdapterPosition(it)
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(snapPosition)
                    if (viewHolder is SelectedImageAdapter.ViewHolder && imageAndVideo[snapPosition].type != ImageAndVideo.TYPE_IMAGE) {
                        if (isVideoViewVisibleEnough(recyclerView, viewHolder, snapHelper)) {
                            viewHolder.videoView.start()
                        } else {
                            viewHolder.videoView.pause()
                        }
                    }
                }
            }
        })
        if(imageAndVideo.size > 1) {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            preUpLoadingRecyclerView.layoutManager = layoutManager
            val scale = resources.displayMetrics.density
            val spacingInPixels = (10 * scale + 0.5f).toInt()
            preUpLoadingRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(spacingInPixels))
        }
        preUpLoadingRecyclerView.adapter = preUpLoadingAdapter
    }

    fun isVideoViewVisibleEnough(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, snapHelper: PagerSnapHelper): Boolean {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager) ?: return false
        val snapPosition = recyclerView.getChildAdapterPosition(snapView)
        val viewHolderPosition = viewHolder.adapterPosition
        return snapPosition == viewHolderPosition
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAG_OTHER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val userList = data?.getSerializableExtra("tagList") as? ArrayList<User>
            tagList.clear()
            userList?.let {
                // Xử lý userList được nhận từ TagOtherActivity
                tagList.addAll(it)
            }
        }
    }
}