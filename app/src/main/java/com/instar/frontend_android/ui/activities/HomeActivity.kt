package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter

class HomeActivity : AppCompatActivity() {
    private lateinit var imageList: List<Images>
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        avatarRecyclerView = findViewById(R.id.recyclerView)
        initView()
    }

    private fun initView() {
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        imageList = getImages()
        newsFollowAdapter = NewsFollowAdapter(imageList)
        avatarRecyclerView.adapter = newsFollowAdapter
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
}
