package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import com.instar.frontend_android.databinding.ActivityProfileBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.PostResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.MyViewPagerAdapter
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.MyPostFragment
import com.instar.frontend_android.ui.fragments.MyPostSavedFragment
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class ProfileActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
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
    private lateinit var btnPostUp: ImageButton
    private lateinit var btnPersonal: View
    private lateinit var btnReel:ImageView
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
        postService = ServiceBuilder.buildService(PostService::class.java, this)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)


        val userintent: User? = intent.getSerializableExtra("user") as? User

        if (userintent != null) {
            updateUserInformation(userintent)
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
    }

    private fun updateUserInformation(user: User) {
        tvTenNguoiDung.text = user.username
        tvNickname.text = user.fullname
        tvDescription.text = user.desc
        tvSoLuongNguoiTheoDoi.text = user.followers?.size.toString()
        tvSoLuongDangTheoDoi.text = user.followings?.size.toString()
        Glide.with(this@ProfileActivity)
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(imgAvatar)
        lifecycleScope.launch {
            try {
                val response1 = getMyPostsData(user.id)
                tvSoLuongBaiViet.text = response1.data?.posts!!.size.toString();

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
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
        btnReel = binding.btnReel
        btnSearch = binding.btnSearch
        btnPostUp = binding.btnPostUp
        btnPersonal = binding.btnPersonal
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
            ServiceBuilder.setRefreshToken(this, null)
            ServiceBuilder.setAccessToken(this, null)

            val intent = Intent(this, LoginOtherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
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
        widthLayout = (getScreenWidth(this) - dpToPx(30 * 4 + 10 * 2 + 37)) / 4
        setMargin(btnSearch)
        setMargin(btnPersonal)
        setMargin(btnReel)
        setMargin(btnPostUp)
    }

    private var widthLayout: Int? = null

    @RequiresApi(Build.VERSION_CODES.R)
    fun setMargin(view: View) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.leftMargin = widthLayout!!
        view.layoutParams = layoutParams
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
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

    private suspend fun getMyPostsData(userId: String): ApiResponse<PostResponse> {
        return withContext(Dispatchers.IO) {
            postService.getAllPostsByUserId(userId).awaitResponse()
        }
    }
}