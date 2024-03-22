package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginBinding
import com.instar.frontend_android.databinding.ActivityProfileBinding
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

class ProfileActivity : AppCompatActivity() {
    private lateinit var userService: UserService

    private lateinit var binding: ActivityProfileBinding
    private lateinit var btn_editProfile : TextView
    private lateinit var tvTenNguoiDung : TextView
    private lateinit var imgAvatar : ImageView

    public var user: User? = null

    suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        loadActivity()
        initView()

        btn_editProfile = binding.btnEditProfile
        tvTenNguoiDung = binding.tvTenNguoiDung

        userService = ServiceBuilder.buildService(UserService::class.java, this)

        val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")

            lifecycleScope.launch {
                try {
                    val response = getUserData(id)
                    user = response.data?.user
                } catch (e: Exception) {
                    // Xử lý ngoại lệ nếu có
                }
            }
        }
    }
    private fun loadActivity() {
        tvTenNguoiDung.text  = user?.username // Lấy tên người dùng và gán vào biến username
    }

    private fun initView() {
        btn_editProfile.setOnClickListener {
            val newPage = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            startActivity(newPage)
        }
    }

}