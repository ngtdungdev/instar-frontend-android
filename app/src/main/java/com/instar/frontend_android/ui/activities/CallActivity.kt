package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.instar.frontend_android.R


class CallActivity : AppCompatActivity() {
    private var mainLayout: RelativeLayout? = null
    private var btnEndCall: ImageButton? = null
    private var layoutComponent: RelativeLayout? = null
    private var frameLayout: FrameLayout? = null
    private var cardView: CardView? = null
    private var imgAvatar: ImageView? = null
    private var time: TextView? = null
    private var yourCamera: ImageView? = null
    private var otherCamera: ImageView? = null
    private var btnCameraOn: ImageButton? = null
    private var btnCameraOff: ImageButton? = null
    private var btnMicOn: ImageButton? = null
    private var btnMicOff: ImageButton? = null
    private var btnSwitchCamera: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            initView()
        }
    }

    private fun initView() {
        mainLayout = findViewById(R.id.main);
        btnEndCall = findViewById(R.id.btnEndCall);
        layoutComponent = findViewById(R.id.layoutComponent);
        frameLayout = findViewById(R.id.frameLayout);
        cardView = findViewById(R.id.cardView);
        imgAvatar = findViewById(R.id.imgAvatar);
        time = findViewById(R.id.time);
        yourCamera = findViewById(R.id.yourCamera);
        otherCamera = findViewById(R.id.otherCamera);
        btnCameraOn = findViewById(R.id.btnCameraOn);
        btnCameraOff = findViewById(R.id.btnCameraOff);
        btnMicOn = findViewById(R.id.btnMicOn);
        btnMicOff = findViewById(R.id.btnMicOff);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
    }
}