package com.instar.frontend_android.ui.activities

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginBinding
import com.instar.frontend_android.databinding.ActivityProfileBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.MyViewPagerAdapter
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.MyPostFragment
import com.instar.frontend_android.ui.fragments.MyPostSavedFragment
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class ProfileActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var binding: ActivityProfileBinding
    private lateinit var btn_editProfile : TextView
    private lateinit var btnHome : ImageButton
    private lateinit var btnSearch : ImageButton
    private lateinit var btnLogout : ImageButton
    private lateinit var btnPostUp1 : ImageButton
    private lateinit var tvTenNguoiDung : TextView
    private lateinit var tvNickname : TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvSoLuongBaiViet: TextView
    private lateinit var tvSoLuongDangTheoDoi: TextView
    private lateinit var tvSoLuongNguoiTheoDoi: TextView
    private lateinit var imgAvatar : ImageView
//    private lateinit var frameAvatar : FrameLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    public var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        // Khởi tạo adapter và gán nó cho ViewPager2
        myViewPagerAdapter = MyViewPagerAdapter(this@ProfileActivity)
        viewPager2.adapter = myViewPagerAdapter

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
//            this@ProfileActivity.frameAvatar = frameAvatar
            this@ProfileActivity.tabLayout = tabLayout
            this@ProfileActivity.viewPager2 = viewPager2
            this@ProfileActivity.btnHome = btnSearch
            this@ProfileActivity.btnSearch = btnSearch
            this@ProfileActivity.btnPostUp1 = btnPostUp1
            this@ProfileActivity.btnLogout = btnLogout
        }
        btn_editProfile.setOnClickListener {
            val newPage = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            startActivity(newPage)
        }
//        frameAvatar.setOnClickListener{
//            val newPost = Intent(this@ProfileActivity, EditProfileActivity::class.java)
//            startActivity(newPost)
//        }
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java);
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            Log.i("hi", "log out ")
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Xử lý khi tab được chọn
                tab?.let {
                    viewPager2.currentItem = it.position
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = wm.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
    }
    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}
