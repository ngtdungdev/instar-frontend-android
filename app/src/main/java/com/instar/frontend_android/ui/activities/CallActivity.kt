package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputLayout
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.User
import androidx.appcompat.app.AlertDialog.Builder;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import android.content.DialogInterface.OnClickListener;
import android.text.Editable
import com.instar.frontend_android.ui.utils.Helpers

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
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)
        val yourUserID = findViewById<TextView>(R.id.your_user_id)
        val yourUserName = findViewById<TextView>(R.id.your_user_name)
        val textInputLayout = findViewById<TextInputLayout>(R.id.target_user_id)
        val user = intent.getSerializableExtra("user") as? User
        val targetUserId = user?.id

        initView()

        val userId = Helpers.getUserId(this)
        yourUserID.text = "Your User ID :" + userId
        yourUserName.text = "Your User Name :" + userId + "_name"

        val editable: Editable = Editable.Factory.getInstance().newEditable(targetUserId)
        textInputLayout.editText?.text = editable

        initVoiceButton()

        initVideoButton()
    }

    private fun initView() {
//        mainLayout = findViewById(R.id.main);
//        btnEndCall = findViewById(R.id.btnEndCall);
//        layoutComponent = findViewById(R.id.layoutComponent);
//        frameLayout = findViewById(R.id.frameLayout);
//        cardView = findViewById(R.id.cardView);
//        imgAvatar = findViewById(R.id.imgAvatar);
//        time = findViewById(R.id.time);
//        yourCamera = findViewById(R.id.yourCamera);
//        otherCamera = findViewById(R.id.otherCamera);
//        btnCameraOn = findViewById(R.id.btnCameraOn);
//        btnCameraOff = findViewById(R.id.btnCameraOff);
//        btnMicOn = findViewById(R.id.btnMicOn);
//        btnMicOff = findViewById(R.id.btnMicOff);
//        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
    }

    private fun initVideoButton() {
        val newVideoCall = findViewById<ZegoSendCallInvitationButton>(R.id.new_video_call)
        newVideoCall.setIsVideoCall(true)

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.resourceID = "zego_data"
        newVideoCall.setOnClickListener { v: View? ->
            val inputLayout = findViewById<TextInputLayout>(R.id.target_user_id)
            val targetUserID = inputLayout.editText!!.getText().toString()
            val split =
                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> = ArrayList()
            for (userID: String in split) {
                val userName = userID + "_name"
                users.add(ZegoUIKitUser(userID, userName))
            }
            newVideoCall.setInvitees(users)
        }
    }

    private fun initVoiceButton() {
        val newVoiceCall = findViewById<ZegoSendCallInvitationButton>(R.id.new_voice_call)
        newVoiceCall.setIsVideoCall(false)


        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.resourceID = "zego_data"
        newVoiceCall.setOnClickListener { v: View? ->
            val inputLayout = findViewById<TextInputLayout>(R.id.target_user_id)
            val targetUserID = inputLayout.editText!!.getText().toString()
            val split =
                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> =
                ArrayList()
            for (userID: String in split) {
                val userName = userID + "_name"
                users.add(ZegoUIKitUser(userID, userName))
            }
            newVoiceCall.setInvitees(users)
        }
    }

    private fun signOut() {
        ZegoUIKitPrebuiltCallService.unInit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val builder = Builder(this@CallActivity)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure to Sign Out?After Sign out you can't receive offline calls")
        builder.setNegativeButton(R.string.call_cancel, object : OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
            }
        })
        builder.setPositiveButton(R.string.call_ok, object : OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
                signOut()
                finish()
            }
        })
        builder.create().show()
    }
}