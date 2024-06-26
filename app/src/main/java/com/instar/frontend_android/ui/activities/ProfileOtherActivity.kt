package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityProfileOtherBinding
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.PostResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.MyViewPagerAdapter
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileOtherActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
    private lateinit var notificationService: NotificationService
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var binding: ActivityProfileOtherBinding
    private lateinit var url: ImageView
    private lateinit var btnHome : ImageButton
    private lateinit var btnSearch : ImageButton
    private lateinit var btnFollow : TextView
    private lateinit var btnIsFollow : TextView
    private lateinit var btnChat : TextView
    private lateinit var tvTenNguoiDung : TextView
    private lateinit var tvNickname : TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvSoLuongBaiViet: TextView
    private lateinit var tvSoLuongDangTheoDoi: TextView
    private lateinit var tvSoLuongNguoiTheoDoi: TextView
    private lateinit var imgAvatar : ImageView
    private lateinit var btnPostUp: ImageButton
    private lateinit var btnPersonal: View
    private lateinit var btnReel: ImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    public var user: User? = null
    public var userOther: User? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileOtherBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        userOther = intent.getSerializableExtra("userOther") as? User

        user = intent.getSerializableExtra("user") as? User

        myViewPagerAdapter = MyViewPagerAdapter(this@ProfileOtherActivity,userOther?.id)
        viewPager2.adapter = myViewPagerAdapter

        userService = ServiceBuilder.buildService(UserService::class.java, this)
        postService = ServiceBuilder.buildService(PostService::class.java, this)
        notificationService = ServiceBuilder.buildService(NotificationService::class.java, this)
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, this)


        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        if (userOther != null) {
            updateUserOtherInformation(userOther!!)
        }

        if (user != null) {
            updateUserInformation(user!!)
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


    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        binding.apply {
            this@ProfileOtherActivity.tvTenNguoiDung = tvTenNguoiDung
            this@ProfileOtherActivity.tvNickname = tvNickname
            this@ProfileOtherActivity.tvDescription = tvDescription
            this@ProfileOtherActivity.tvSoLuongBaiViet = tvSoLuongBaiViet
            this@ProfileOtherActivity.tvSoLuongNguoiTheoDoi = tvSoLuongNguoiTheoDoi
            this@ProfileOtherActivity.tvSoLuongDangTheoDoi = tvSoLuongDangTheoDoi
            this@ProfileOtherActivity.imgAvatar = imgAvatar
            this@ProfileOtherActivity.tabLayout = tabLayout
            this@ProfileOtherActivity.viewPager2 = viewPager2
            this@ProfileOtherActivity.btnHome = btnHome
            this@ProfileOtherActivity.btnSearch = btnSearch
            this@ProfileOtherActivity.btnFollow = btnFollow
            this@ProfileOtherActivity.btnIsFollow = btnIsFollow
            this@ProfileOtherActivity.btnChat = btnChat
            this@ProfileOtherActivity.url = url
        }
        btnReel = binding.btnReel
        btnSearch = binding.btnSearch
        btnPostUp = binding.btnPostUp
        btnPersonal = binding.btnPersonal

        btnHome.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java);
            startActivity(intent)
            finish()
        }
        btnFollow.setOnClickListener {
            userOther?.id?.let { it1 ->
                userService.follow(it1).handleResponse(
                    onSuccess = {
                        user?.followings = it.data?.user?.followings!!

                        lifecycleScope.launch {
                            try {
                                val response = getUserData(it1)
                                userOther = response.data?.user
                                userOther?.let { it3 ->
                                    updateUserOtherInformation(it3)

                                    if (it.data.user?.followings?.contains(userOther?.id) == true) {
                                        btnFollow.visibility = View.GONE
                                        btnIsFollow.visibility = View.VISIBLE
                                        btnChat.visibility = View.VISIBLE
                                        sendNotification()
                                    } else {
                                        btnIsFollow.visibility = View.GONE
                                        btnFollow.visibility = View.VISIBLE
                                        btnChat.visibility = View.GONE
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    },
                    onError = {

                    }
                )
            }
        }
        btnIsFollow.setOnClickListener {
            userOther?.id?.let { it1 ->
                userService.follow(it1).handleResponse(
                    onSuccess = {
                        lifecycleScope.launch {
                            try {
                                user?.followings = it.data?.user?.followings!!

                                println("123123")

                                val response = getUserData(it1)
                                userOther = response.data?.user
                                userOther?.let { it3 ->
                                    updateUserOtherInformation(it3)

                                    if (it.data.user?.followings?.contains(userOther?.id) == true) {
                                        btnFollow.visibility = View.GONE
                                        btnIsFollow.visibility = View.VISIBLE
                                        btnChat.visibility = View.VISIBLE
                                    } else {
                                        btnIsFollow.visibility = View.GONE
                                        btnFollow.visibility = View.VISIBLE
                                        btnChat.visibility = View.GONE

                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onError = {

                    }
                )
            }
        }
        btnChat.setOnClickListener {
            val intent = Intent(this@ProfileOtherActivity, DirectMessageActivity::class.java)
            val members = arrayOf(user?.id, userOther?.id)
            members.sort()
            intent.putExtra("chatID", members.joinToString("-"))
            startActivity(intent)
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


        btnHome.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java);
            startActivity(intent)
            finish()
        }

        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java);
            startActivity(intent)
            finish()
        }

        btnPostUp.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java);
            intent.putExtra("showPostFragment", true)
            startActivity(intent)
            finish()
        }
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
    private fun updateUserOtherInformation(userOther: User) {
        tvTenNguoiDung.text = userOther.username
        tvNickname.text = userOther.fullname
        tvDescription.text = userOther.desc
        tvSoLuongNguoiTheoDoi.text = userOther.followers.size.toString()
        tvSoLuongDangTheoDoi.text = userOther.followings.size.toString()

        Glide.with(this@ProfileOtherActivity)
            .load(userOther.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(imgAvatar)

        lifecycleScope.launch {
            try {
                val response1 = getMyPostsData(userOther.id)
                tvSoLuongBaiViet.text = response1.data?.posts!!.size.toString();
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun updateUserInformation(user: User) {
        Glide.with(this@ProfileOtherActivity)
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(url)
        if (user.followings.contains(userOther?.id) == true) {
            btnFollow.visibility = View.GONE
            btnIsFollow.visibility = View.VISIBLE
        } else {
            btnIsFollow.visibility = View.GONE
            btnFollow.visibility = View.VISIBLE
            btnChat.visibility = View.GONE
        }
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

    private suspend fun sendNotification() {
        return withContext(Dispatchers.IO) {
            val notificationRequest = NotificationRequest(null, null,
                user?.id, userOther!!.id, "${user?.username} đã bắt đầu theo dõi bạn", "follow")

            notificationService.createNotification(userOther!!.id, notificationRequest).awaitResponse()

            fcmNotificationService.sendFollowNotification(notificationRequest).awaitResponse()
        }
    }

    private suspend fun getMyPostsData(userId: String): ApiResponse<PostResponse> {
        return withContext(Dispatchers.IO) {
            postService.getAllPostsByUserId(userId).awaitResponse()
        }
    }
}