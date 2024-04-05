package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var btnBack: ImageView
    private lateinit var edtFullname: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtIntroduction: EditText
    private lateinit var imageAvatar: ImageView
    private lateinit var btnEditAvatar: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

//        if (accessToken != null) {
//            val decodedTokenJson = Helpers.decodeJwt(accessToken)
//            val id = decodedTokenJson.getString("id")
//            lifecycleScope.launch {
//                try {
//                    val response = getUserData(id)
//                    val user = response.data?.user
//                    if (user != null) {
//
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }

        btnBack = findViewById(R.id.btnBack)
        edtFullname = findViewById(R.id.edtFullname)
        edtUsername = findViewById(R.id.edtUsername)
        edtIntroduction = findViewById(R.id.edtIntroduction)
        imageAvatar = findViewById(R.id.imageAvatar)
        btnEditAvatar = findViewById(R.id.customButton)

        btnBack.setOnClickListener {
            // Xử lý cập nhật lại thông tin người dùng nếu như có chỉnh sửa
            val newPage = Intent(this@EditProfileActivity, ProfileActivity::class.java)
            startActivity(newPage)
        }
        btnEditAvatar.setOnClickListener {
            val newPage = Intent(this@EditProfileActivity, UpdateAvatarActivity::class.java)
            startActivity(newPage)
        }
        updateUserInformation()
    }
    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
    private fun updateUserInformation() {
        val username = intent.getStringExtra("username")
        val fullname = intent.getStringExtra("fullname")
        val description = intent.getStringExtra("description")
        val uri = intent.getStringExtra("avatarUri")
        edtUsername.setText(username)
        edtFullname.setText(fullname)
        edtIntroduction.setText(description)
        Glide.with(this@EditProfileActivity)
            .load(uri)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(imageAvatar)
    }
}