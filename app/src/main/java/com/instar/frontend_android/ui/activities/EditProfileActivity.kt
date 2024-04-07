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
import com.instar.frontend_android.ui.DTO.ImageAndVideo
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
    private lateinit var edtPassword: EditText
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
        edtPassword = findViewById(R.id.edtPassword)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        var user = intent.getSerializableExtra("user") as? User

        if (user != null) {
            updateUserInformation(user)
        } else {
            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")
                lifecycleScope.launch {
                    try {
                        val response = getUserData(id)
                        user = response.data?.user
                        user?.let { updateUserInformation(it) }
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
                user?.password = edtPassword.text.toString();

                if (user != null) {
                    val imageAvatarUrl = intent.getSerializableExtra("image") as? ImageAndVideo;
                    lifecycleScope.launch {
                        update(id, user!!, imageAvatarUrl!!)
                        Toast.makeText(applicationContext, imageAvatarUrl.toString(), Toast.LENGTH_LONG).show()
                    }


                };
            }
        }
    }

    suspend fun update(id: String, user: User?, imageAvatarUrl: ImageAndVideo?) {
        if (imageAvatarUrl != null) {
            userService.updateUser(id, user!!, Helpers.convertToMultipartPart(applicationContext, imageAvatarUrl)).handleResponse(
                onSuccess = {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    Toast.makeText(this, "Sửa thông tin thành công", Toast.LENGTH_LONG).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this, "Sửa thông tin lỗi hoặc tên trùng với tài khoản khác", Toast.LENGTH_LONG).show()
                })
        } else {
            userService.updateUserV2(id, user!!).handleResponse(
                onSuccess = {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    Toast.makeText(this, "Sửa thông tin thành công", Toast.LENGTH_LONG).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this, "Sửa thông tin lỗi hoặc tên trùng với tài khoản khác", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun updateUserInformation(user: User) {
        edtUsername.setText(user.username)
        edtFullname.setText(user.fullname)
        edtIntroduction.setText(user.desc)
        edtPassword.setText(user.password)

        val imageAvatarUrl = intent.getSerializableExtra("image") as? ImageAndVideo

        if(imageAvatarUrl == null) {
            println(user.profilePicture?.url)
            Glide.with(this@EditProfileActivity)
                .load(user.profilePicture?.url)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageAvatar)
        }
        else{
            Glide.with(this@EditProfileActivity)
                .load(imageAvatarUrl.uri)
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