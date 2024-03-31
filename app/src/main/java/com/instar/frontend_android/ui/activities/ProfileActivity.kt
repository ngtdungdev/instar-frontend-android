package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
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
    private lateinit var tvNickname : TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvSoLuongBaiViet: TextView
    private lateinit var tvSoLuongDangTheoDoi: TextView
    private lateinit var tvSoLuongNguoiTheoDoi: TextView
    private lateinit var imgAvatar : ImageView
    public var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        userService = ServiceBuilder.buildService(UserService::class.java, this)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")

            lifecycleScope.launch {
                try {
                    val response = getUserData(id)
                    user = response.data?.user
                    tvTenNguoiDung.text  = user?.username // Set username if available
                    tvNickname.text  = user?.fullname
                    tvDescription.text = user?.desc
//                    tvSoLuongBaiViet
                    tvSoLuongNguoiTheoDoi.text = user?.followers?.size.toString()
                    tvSoLuongDangTheoDoi.text = user?.followings?.size.toString()
                    Glide.with(this@ProfileActivity)
                        .load(response.data?.user?.profilePicture?.url)
                        .placeholder(R.drawable.default_image) // Placeholder image
                        .error(R.drawable.default_image) // Image to display if load fails
                        .into(imgAvatar)
                } catch (e: Exception) {
                    // Handle exceptions, e.g., log or show error to user
                    e.printStackTrace()
                }
            }
        }
    }

    private fun initView() {
        binding.apply {
            btn_editProfile = btnEditProfile
            this@ProfileActivity.tvTenNguoiDung = tvTenNguoiDung
            this@ProfileActivity.tvNickname = tvNickname
            this@ProfileActivity.tvDescription = tvDescription
            this@ProfileActivity.tvSoLuongBaiViet = tvSoLuongBaiViet
            this@ProfileActivity.tvSoLuongNguoiTheoDoi = tvSoLuongNguoiTheoDoi
            this@ProfileActivity.tvSoLuongDangTheoDoi = tvSoLuongDangTheoDoi
            this@ProfileActivity.imgAvatar = imgAvatar
        }
        btn_editProfile.setOnClickListener {
            val newPage = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            startActivity(newPage)
        }
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}
