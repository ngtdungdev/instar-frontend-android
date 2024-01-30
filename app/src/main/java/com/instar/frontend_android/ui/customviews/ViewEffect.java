package com.instar.frontend_android.ui.customviews;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.instar.frontend_android.R;

public class ViewEffect {
    public ViewEffect() {

    }

    public static void ImageBack(ImageButton imageButton) {
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        imageButton.setBackgroundResource(R.drawable.ic_instagram_back_down);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        imageButton.setBackgroundResource(R.drawable.ic_instagram_back_up);
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public static void ViewText(TextView textView) {
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        textView.setAlpha(0.7f);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        textView.setAlpha(1f);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static void ViewButton(ImageButton imageButton, TextView textView) {
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        imageButton.setBackgroundResource(R.drawable.selector_btn_color_login_down);
                        textView.setTextColor(Color.parseColor("#7986FF"));
                        applyValueAnimation(textView,14,13.8f);
                        applyScaleAnimation(imageButton,1.0f,0.97f,1.0f,0.97f);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        imageButton.setBackgroundResource(R.drawable.selector_btn_color_login_up);
                        textView.setTextColor(Color.parseColor("#4558FF"));
                        applyValueAnimation(textView,13.7f,14);
                        applyScaleAnimation(imageButton,0.97f, 1.0f, 0.97f, 1.0f);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private static void applyScaleAnimation(View button, float fromX, float toX, float fromY, float toY) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromX, toX,
                fromY, toY,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);
        button.startAnimation(scaleAnimation);
    }
    private static void applyValueAnimation(TextView textView, float startSize, float endSize) {
        ValueAnimator animator = ValueAnimator.ofFloat(startSize, endSize);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue);
            }
        });
        animator.start();
    }
}
