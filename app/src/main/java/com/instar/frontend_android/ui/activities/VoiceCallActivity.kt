package com.instar.frontend_android.ui.activities

import android.R
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class VoiceCallActivity : AppCompatActivity() {
    // Định nghĩa các thành phần trong layout
    private var mainLayout: RelativeLayout? = null
    private var btnEndCall: ImageButton? = null
    private var layoutComponent: RelativeLayout? = null
    private var frameLayout: FrameLayout? = null
    private var cardView: CardView? = null
    private var imgAvatar: ImageView? = null
    private var time: TextView? = null
    private var yourCamera: ImageView? = null
    private var ly: LinearLayout? = null
    private var btnCameraOn: ImageButton? = null
    private var btnCameraOff: ImageButton? = null
    private var btnMicOn: ImageButton? = null
    private var btnMicOff: ImageButton? = null
    private var btnSwitchCamera: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)

        // Gọi hàm initView để ánh xạ các thành phần
        initView()
    }

    // Hàm để ánh xạ các thành phần trong layout
    private fun initView() {
        mainLayout = findViewById<RelativeLayout>(R.id.main)
        btnEndCall = findViewById<ImageButton>(R.id.btnEndCall)
        layoutComponent = findViewById<RelativeLayout>(R.id.layoutComponent)
        frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        cardView = findViewById<CardView>(R.id.cardView)
        imgAvatar = findViewById<ImageView>(R.id.imgAvatar)
        time = findViewById<TextView>(R.id.time)
        yourCamera = findViewById<ImageView>(R.id.yourCamera)
        ly = findViewById<LinearLayout>(R.id.ly)
        btnCameraOn = findViewById<ImageButton>(R.id.btnCameraOn)
        btnCameraOff = findViewById<ImageButton>(R.id.btnCameraOff)
        btnMicOn = findViewById<ImageButton>(R.id.btnMicOn)
        btnMicOff = findViewById<ImageButton>(R.id.btnMicOff)
        btnSwitchCamera = findViewById<ImageButton>(R.id.btnSwitchCamera)
    }
}

