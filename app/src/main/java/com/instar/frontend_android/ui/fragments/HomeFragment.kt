package com.instar.frontend_android.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentHomeBinding
import com.instar.frontend_android.ui.DTO.Feeds
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.adapters.NewsFeedAdapter
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.services.AuthService

class HomeFragment : Fragment() {
    private lateinit var imageList: List<Images>
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    private lateinit var feedList: List<Feeds>
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var feedsRecyclerView: RecyclerView

    private lateinit var btnMessage: ImageView

    private lateinit var authService: AuthService
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Toast.makeText(context,"aaa",Toast.LENGTH_SHORT).show()
//        authService = ServiceBuilder.buildService(AuthService::class.java, this)
//
//        authService.profile().handleResponse(
//            onSuccess = { authResponse ->
//                // Khởi tạo SharedPreferences
//                Log.d("Profile", authResponse.data.toString())
//            },
//            onError = { error ->
//                // Handle error
//                val message = error.message;
//                Log.e("ServiceBuilder", "Error: $message - ${error.status}")
//
//                if (error.status == 401) {
//                    ServiceBuilder.setRefreshToken(this, null)
//                    ServiceBuilder.setAccessToken(this, null)
//                }
//
//                val intent = Intent(this@HomeActivity, LoginOtherActivity::class.java)
//                startActivity(intent)
//            }
//        )
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        avatarRecyclerView = binding.stories
        feedsRecyclerView = binding.newsfeed
        btnMessage = binding.iconMessenger
        initView()
        return binding.root
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
        feedsRecyclerView.layoutManager = LinearLayoutManager(context)
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