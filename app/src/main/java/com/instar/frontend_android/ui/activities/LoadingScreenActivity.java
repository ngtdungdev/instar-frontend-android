package com.instar.frontend_android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.instar.frontend_android.R;

public class LoadingScreenActivity extends AppCompatActivity {
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        progressBar1 = findViewById(R.id.spin_kit1);
        progressBar2 = findViewById(R.id.spin_kit2);
        Sprite doubleBounce1 = new FadingCircle();
        progressBar1.setIndeterminateDrawable(doubleBounce1);
        Sprite doubleBounce2 = new Circle();
        progressBar2.setIndeterminateDrawable(doubleBounce2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingScreenActivity.this, LoginOtherActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

}
