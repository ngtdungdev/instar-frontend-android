package com.instar.frontend_android.ui.activities;

import android.net.http.HttpException;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEffect;

public class LoginEmailActivity extends AppCompatActivity {
    private ImageButton imageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        imageBack = findViewById(R.id.imageBack);
        initView();
    }

    public void initView() {
        effectClick();
    }

    public void effectClick() {
        ViewEffect.ImageBack(imageBack);
    }
}