package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Feeds
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.adapters.NewsFeedAdapter
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse

class HomeActivity : AppCompatActivity() {
    private lateinit var imageList: List<Images>
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    private lateinit var feedList: List<Feeds>
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var feedsRecyclerView: RecyclerView

    private lateinit var btnMessage: ImageView

    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        authService = ServiceBuilder.buildService(AuthService::class.java, this)

        authService.profile().handleResponse(
            onSuccess = { authResponse ->
                // Khởi tạo SharedPreferences
                Log.d("Profile", authResponse.data.toString())
            },
            onError = { error ->
                // Handle error
                val message = error.message;
                Log.e("ServiceBuilder", "Error: $message - ${error.status}")

                if (error.status == 401) {
                    ServiceBuilder.setRefreshToken(this, null)
                    ServiceBuilder.setAccessToken(this, null)
                }

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
        imageList = getImages()
        newsFollowAdapter = NewsFollowAdapter(imageList)
        avatarRecyclerView.adapter = newsFollowAdapter


        feedList = getFeeds()
        newsFeedAdapter = NewsFeedAdapter(feedList)
        feedsRecyclerView.layoutManager = LinearLayoutManager(this)
        feedsRecyclerView.adapter = newsFeedAdapter
    }

    private fun getImages(): ArrayList<Images> {
        val imageList = ArrayList<Images>()
        val image0 = Images(Images.TYPE_PERSONAL_AVATAR, "Tin của bạn", R.mipmap.ic_instagram_icon_skullcap)
        val image1 = Images(Images.TYPE_FRIEND_AVATAR, "Duy ko rep", R.mipmap.no1)
        val image2 = Images(Images.TYPE_FRIEND_AVATAR, "Hiếu no Hope", R.mipmap.no2)
        val image3 = Images(Images.TYPE_FRIEND_AVATAR, "Hưng đi làm", R.mipmap.no3)
        imageList.add(image0)
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
