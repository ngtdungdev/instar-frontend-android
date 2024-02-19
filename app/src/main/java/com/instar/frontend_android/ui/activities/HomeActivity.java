package com.instar.frontend_android.ui.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.DTO.Images;
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<Images> imageList;
    private NewsFollowAdapter newsFollowAdapter;
    private RecyclerView avatarRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        avatarRecyclerView = findViewById(R.id.recyclerView);
        initView();
    }

    public void initView() {
        loadRecyclerView();
    }

    public void loadRecyclerView() {
        imageList = getImages();
        newsFollowAdapter = new NewsFollowAdapter(imageList);
        avatarRecyclerView.setAdapter(newsFollowAdapter);
    }

    public ArrayList<Images> getImages() {
        ArrayList<Images> imageList = new ArrayList<Images>();
        Images image0 = new Images(Images.TYPE_PERSONAL_AVATAR,"Tin của bạn",R.mipmap.ic_instagram_icon_skullcap);
        Images image1 = new Images(Images.TYPE_FRIEND_AVATAR,"Duy ko rep",R.mipmap.no1);
        Images image2 = new Images(Images.TYPE_FRIEND_AVATAR,"Hiếu no Hope",R.mipmap.no2);
        Images image3 = new Images(Images.TYPE_FRIEND_AVATAR,"Hưng đi làm",R.mipmap.no3);
        imageList.add(image0);
        imageList.add(image1);
        imageList.add(image2);
        imageList.add(image3);
        return imageList;
    }
}
