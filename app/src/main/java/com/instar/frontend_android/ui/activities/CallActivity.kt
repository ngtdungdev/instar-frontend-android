package com.instar.frontend_android.ui.activities

import android.Manifest
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
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import androidx.appcompat.app.AlertDialog.Builder;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import org.json.JSONObject
import timber.log.Timber
import android.content.DialogInterface.OnClickListener;

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
        val user = intent.getSerializableExtra("user") as? User
        val userID = user?.id
        val userName = user?.username

        initView()

        yourUserID.text = "Your User ID :" + userID;
        yourUserName.text = "Your User Name :" + userName;

        val appID: Long = 1574470634;
        val appSign: String = "a5d41bb837834dd85b9c4b7bb00f15e20d11f2f2d77012957dc7839b76a30331";


        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                val message =
                    "We need your consent for the following permissions in order to use the offline call function properly"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }).request(RequestCallback { allGranted, grantedList, deniedList -> })


        initCallInviteService(appID, appSign, userID, userName)

        initVoiceButton()

        initVideoButton()

        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                val message =
                    "We need your consent for the following permissions in order to use the offline call function properly"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }).request(RequestCallback { allGranted, grantedList, deniedList -> })

//        ZegoUIKitPrebuiltCallInvitationService.unInit()
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
            val users: MutableList<ZegoUIKitUser> =
                ArrayList()
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

    fun initCallInviteService(appID: Long, appSign: String?, userID: String?, userName: String?) {
        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
        callInvitationConfig.provider =
            ZegoUIKitPrebuiltCallConfigProvider { invitationData -> getConfig(invitationData) }
        ZegoUIKitPrebuiltCallService.events.errorEventsListener =
            ErrorEventsListener { errorCode, message -> Timber.d("onError() called with: errorCode = [$errorCode], message = [$message]") }
        ZegoUIKitPrebuiltCallService.events.invitationEvents.pluginConnectListener =
            SignalPluginConnectListener { state, event, extendedData ->
                Timber.d(
                    "onSignalPluginConnectionStateChanged() called with: state = [" + state + "], event = [" + event
                            + "], extendedData = [" + extendedData + "]"
                )
            }
        ZegoUIKitPrebuiltCallService.init(
            application, appID, appSign, userID, userName,
            callInvitationConfig
        )
        ZegoUIKitPrebuiltCallService.events.callEvents.callEndListener =
            CallEndListener { callEndReason, jsonObject ->
                Timber.d(
                    "onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                            + "]"
                )
            }
        ZegoUIKitPrebuiltCallService.events.callEvents.setExpressEngineEventHandler(
            object : IExpressEngineEventHandler() {
                override fun onRoomStateChanged(
                    roomID: String, reason: ZegoRoomStateChangedReason, errorCode: Int,
                    extendedData: JSONObject
                ) {
                    Timber.d(
                        "onRoomStateChanged() called with: roomID = [" + roomID + "], reason = [" + reason
                                + "], errorCode = [" + errorCode + "], extendedData = [" + extendedData + "]"
                    )
                }
            })
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

    fun getConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig {
        val isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.value
        val isGroupCall = invitationData.invitees.size > 1
        val callConfig: ZegoUIKitPrebuiltCallConfig
        callConfig = if (isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVideoCall()
        } else if (!isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVoiceCall()
        } else if (!isVideoCall) {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall()
        } else {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
        }
        return callConfig
    }

    override fun onDestroy() {
        super.onDestroy()
        // when use minimize feature,it you swipe close this activity,call endCall()
        // to make sure call is ended and the float window is dismissed
        ZegoUIKitPrebuiltCallService.endCall()
    }
}