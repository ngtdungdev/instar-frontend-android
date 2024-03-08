package com.instar.frontend_android.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentHomeBinding
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Feeds
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.activities.LoginOtherActivity
import com.instar.frontend_android.ui.adapters.NewsFeedAdapter
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse


class HomeFragment : Fragment() {
    private lateinit var imageList: ArrayList<Images>
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    private lateinit var feedList: ArrayList<Feeds>
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var feedsRecyclerView: RecyclerView

    private lateinit var btnMessage: ImageView
    private lateinit var binding: FragmentHomeBinding

    private lateinit var authService: AuthService
    private lateinit var user: UserResponse

    interface OnItemClickListener {
        fun onItemClick(position: Int?)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        authService = ServiceBuilder.buildService(AuthService::class.java, requireContext())
        avatarRecyclerView = binding.stories
        feedsRecyclerView = binding.newsfeed
        btnMessage = binding.iconMessenger

        initView()
        authService.profile().handleResponse(
            onSuccess = { response ->
                user = response.data
                val avatarUrl = response.data.profilePicture?.url
                imageList = getImages()

                val image0 = Images(
                    Images.TYPE_PERSONAL_AVATAR,
                    "Tin của bạn",
                    avatarUrl
                )
                imageList.add(0, image0)
                newsFollowAdapter = NewsFollowAdapter(context,imageList)
                avatarRecyclerView.adapter = newsFollowAdapter
                // Khởi tạo SharedPreferences
            },
            onError = { error ->
                // Handle error
                val message = error.message;
                Log.e("ServiceBuilder", "Error: $message - ${error.status}")

                ServiceBuilder.setRefreshToken(requireContext(), null)
                ServiceBuilder.setAccessToken(requireContext(), null)

//                val intent = Intent(context, LoginOtherActivity::class.java)
//                startActivity(intent)
            }
        )

        return binding.root
    }

    private fun initView() {
        btnMessage.setOnClickListener {
            if (listener != null) {
                listener!!.onItemClick(2)
            }
        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        feedList = getFeeds()
        newsFeedAdapter = NewsFeedAdapter(feedList)
        feedsRecyclerView.layoutManager = LinearLayoutManager(context)
        feedsRecyclerView.adapter = newsFeedAdapter
    }

    private fun getImages(): ArrayList<Images> {
        val imageList = ArrayList<Images>()
        val image1 = Images(Images.TYPE_FRIEND_AVATAR, "Duy ko rep", null)
        val image2 = Images(Images.TYPE_FRIEND_AVATAR, "Hiếu no Hope", null)
        val image3 = Images(Images.TYPE_FRIEND_AVATAR, "Hưng đi làm", null)
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