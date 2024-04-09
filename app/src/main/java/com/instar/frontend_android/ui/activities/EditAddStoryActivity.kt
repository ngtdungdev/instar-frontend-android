package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.StoryService
import com.instar.frontend_android.ui.utils.Helpers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditAddStoryActivity : AppCompatActivity() {
    private lateinit var imageStory: ImageView
    private lateinit var imgBack: ImageButton
    private lateinit var imgNext: ImageButton
    private lateinit var storyService: StoryService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_add_story)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storyService = ServiceBuilder.buildService(StoryService::class.java, this)

        imageStory = findViewById(R.id.imageStory)
        imgBack = findViewById(R.id.imgBack)
        imgNext = findViewById(R.id.imgNext)

        val imageUri = intent.getStringExtra("imageUri")

        val file = File(imageUri)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files", file.name, requestFile)
        Glide.with(this).load(imageUri).into(imageStory)

        imgBack.setOnClickListener {
            onBackPressed() // Kích hoạt hành động trở về Activity trước đó
        }
        imgNext.setOnClickListener {
            // xử lý để đăng story
            val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("accessToken", null)
            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")

                storyService.createStory(id, part).handleResponse(
                    onSuccess = {
                        val intent = Intent(this, MainScreenActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    },
                    onError = {
                        println(it)
                        println(it.status)
                        Toast.makeText(this, "Đăng lỗi", Toast.LENGTH_LONG).show()
                    }
                );
            }
        }
    }
}

