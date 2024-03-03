package com.instar.frontend_android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Feeds
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.adapters.NewsFeedAdapter
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse

class HomeActivity : AppCompatActivity() {
    private lateinit var imageList: ArrayList<Images>
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    private lateinit var feedList: ArrayList<Feeds>
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var feedsRecyclerView: RecyclerView

    private lateinit var btnMessage: ImageView

    private lateinit var authService: AuthService
    private lateinit var user: UserResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        authService = ServiceBuilder.buildService(AuthService::class.java, this)

        authService.profile().handleResponse(
            onSuccess = { response ->
                user = response.data
                val avatarUrl = response.data.profilePicture?.url
                imageList = getImages()

                val image0 = Images(Images.TYPE_PERSONAL_AVATAR, "Tin của bạn", R.mipmap.ic_instagram_icon_skullcap)
                imageList.add(0, image0)

                newsFollowAdapter = NewsFollowAdapter(imageList)
                avatarRecyclerView.adapter = newsFollowAdapter
                // Khởi tạo SharedPreferences
            },
            onError = { error ->
                // Handle error
                val message = error.message;
                Log.e("ServiceBuilder", "Error: $message - ${error.status}")

                ServiceBuilder.setRefreshToken(this, null)
                ServiceBuilder.setAccessToken(this, null)

                val intent = Intent(this@HomeActivity, LoginOtherActivity::class.java)
                startActivity(intent)
            }
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        avatarRecyclerView = findViewById(R.id.stories)
        feedsRecyclerView = findViewById(R.id.newsfeed)
        btnMessage = findViewById(R.id.iconMessenger);
        initView()
    }

    private fun initView() {
        btnMessage.setOnClickListener {

        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        feedList = getFeeds()
        newsFeedAdapter = NewsFeedAdapter(feedList)
        feedsRecyclerView.layoutManager = LinearLayoutManager(this)
        feedsRecyclerView.adapter = newsFeedAdapter
    }

    private fun getImages(): ArrayList<Images> {
        val imageList = ArrayList<Images>()
        val image1 = Images(Images.TYPE_FRIEND_AVATAR, "Duy ko rep", R.mipmap.no1)
        val image2 = Images(Images.TYPE_FRIEND_AVATAR, "Hiếu no Hope", R.mipmap.no2)
        val image3 = Images(Images.TYPE_FRIEND_AVATAR, "Hưng đi làm", R.mipmap.no3)
        imageList.add(image1)
        imageList.add(image2)
        imageList.add(image3)

        return imageList
    }

    private fun getFeeds(): ArrayList<Feeds> {
        val feedsList = ArrayList<Feeds>()
        val feed1 = Feeds("Tin của bạn","conmeo")
        val feed2 = Feeds("Duy ko rep", "conmeo")
        feedsList.add(feed1)
        feedsList.add(feed2)
        return feedsList
    }
}
