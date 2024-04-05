package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
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
    private lateinit var btnSaveProfile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userService = ServiceBuilder.buildService(UserService::class.java, this);

        btnBack = findViewById(R.id.btnBack)
        edtFullname = findViewById(R.id.edtFullname)
        edtUsername = findViewById(R.id.edtUsername)
        edtIntroduction = findViewById(R.id.edtIntroduction)
        imageAvatar = findViewById(R.id.imageAvatar)
        btnEditAvatar = findViewById(R.id.customButton)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        val user = intent.getSerializableExtra("user") as? User
        Toast.makeText(this, intent.getStringExtra("newurl"), Toast.LENGTH_SHORT).show()
        if (user != null) {
            updateUserInformation(user)
        } else {
            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")
                lifecycleScope.launch {
                    try {
                        val response = getUserData(id)
                        val user = response.data?.user
                        if (user != null) {
                            updateUserInformation(user)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        btnBack.setOnClickListener {
            val newPage = Intent(this@EditProfileActivity, ProfileActivity::class.java)
            startActivity(newPage)
            finish()
        }

        btnEditAvatar.setOnClickListener {
            val newPage = Intent(this@EditProfileActivity, UpdateAvatarActivity::class.java)
            startActivity(newPage)
            finish()
        }

        btnSaveProfile.setOnClickListener {
            // xử lý sự kiện lưu thay đổi use
            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")

                user?.username = edtUsername.text.toString();
                user?.fullname = edtFullname.text.toString();
                user?.desc = edtIntroduction.text.toString();
                user?.profilePicture?.url = intent.getStringExtra("newurl")

                if (user != null) {
                    userService.updateUser(id, user, null).handleResponse(
                        onSuccess = {
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        },
                        onError = {
                            Toast.makeText(this, "Sửa thông tin lỗi hoặc tên trùng với tài khoản khác", Toast.LENGTH_LONG).show()
                        }
                    )
                };
            }

        }
    }

    private fun updateUserInformation(user: User) {
        edtUsername.setText(user.username)
        edtFullname.setText(user.fullname)
        edtIntroduction.setText(user.desc)
        if(intent.getStringExtra("newurl") == null){
            Glide.with(this@EditProfileActivity)
                .load(user.profilePicture?.url)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageAvatar)
        }
        else{
            Glide.with(this@EditProfileActivity)
                .load(intent.getStringExtra("newurl"))
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageAvatar)
        }
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}