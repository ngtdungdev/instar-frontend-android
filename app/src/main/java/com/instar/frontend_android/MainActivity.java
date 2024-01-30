package com.instar.frontend_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.instar.frontend_android.ui.activities.LoadingScreenActivity;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoadingScreenActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}