package com.instar.frontend_android.ui.customviews

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.instar.frontend_android.R

class ViewEffect {
    companion object {
        @SuppressLint("ClickableViewAccessibility")
        fun ImageBack(imageButton: ImageButton) {
            imageButton.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imageButton.setBackgroundResource(R.drawable.ic_instagram_back_down)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        imageButton.setBackgroundResource(R.drawable.ic_instagram_back_up)
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        imageButton.setBackgroundResource(R.drawable.ic_instagram_back_up)
                        true
                    }
                    else -> false
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun ViewText(textView: TextView) {
            textView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        textView.alpha = 0.7f
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        textView.alpha = 1f
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        textView.alpha = 1f
                        true
                    }
                    else -> false
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun ViewMessage(view: View) {
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.setBackgroundColor(Color.parseColor("#FFDDDDDD"))
                        false
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        false
                    }
                    else -> false
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun ViewButton(imageButton: ImageButton, textView: TextView) {
            imageButton.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imageButton.alpha = 0.7f
                        textView.alpha = 0.7f
                        applyValueAnimation(textView, 14f, 13.8f)
                        applyScaleAnimation(imageButton, 1.0f, 0.97f, 1.0f, 0.97f)
                        false
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        imageButton.alpha = 1f
                        textView.alpha = 1f
                        applyValueAnimation(textView, 13.7f, 14f)
                        applyScaleAnimation(imageButton, 0.97f, 1.0f, 0.97f, 1.0f)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        imageButton.alpha = 1f
                        textView.alpha = 1f
                        applyValueAnimation(textView, 13.7f, 14f)
                        applyScaleAnimation(imageButton, 0.97f, 1.0f, 0.97f, 1.0f)
                        false
                    }
                    else -> false
                }
            }
        }

        private fun applyScaleAnimation(button: View, fromX: Float, toX: Float, fromY: Float, toY: Float) {
            val scaleAnimation = ScaleAnimation(
                fromX, toX,
                fromY, toY,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            scaleAnimation.duration = 500
            scaleAnimation.fillAfter = true
            button.startAnimation(scaleAnimation)
        }

        private fun applyValueAnimation(textView: TextView, startSize: Float, endSize: Float) {
            val animator = ValueAnimator.ofFloat(startSize, endSize)
            animator.duration = 500
            animator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue)
            }
            animator.start()
        }
    }
}
