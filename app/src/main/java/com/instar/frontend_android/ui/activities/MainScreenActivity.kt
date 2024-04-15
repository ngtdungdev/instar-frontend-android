package com.instar.frontend_android.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityMainScreenBinding
import com.instar.frontend_android.ui.adapters.ScreenSlidePagerAdapter
import com.instar.frontend_android.ui.fragments.PostFragment
import com.instar.frontend_android.ui.services.FCMService
import com.instar.frontend_android.ui.services.OnFragmentClickListener
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import org.json.JSONObject
import timber.log.Timber
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class MainScreenActivity: AppCompatActivity(), OnFragmentClickListener{
    private lateinit var binding : ActivityMainScreenBinding
    private lateinit var viewPager : ViewPager2
    private var savePosition: Int = 0
    companion object {
        const val REQUEST_CODE = 201
        const val PERMISSION_CODE = 1001
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && Environment.isExternalStorageManager()) {
                initView()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initView()
            } else {
                requestPermission()
            }
        } else {
            initView()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                FCMService(this@MainScreenActivity).getFirebaseCloudMessagingToken()
            } else {
                ActivityCompat.requestPermissions(this@MainScreenActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
            }
        } else {
            FCMService(this@MainScreenActivity).getFirebaseCloudMessagingToken()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("package:$packageName")
            requestPermissionLauncher.launch(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            requestPermissionLauncher.launch(intent)
        }
    }

    private fun initView() {
        val appID: Long = getString(R.string.app_id).toLong();
        val appSign = getString(R.string.app_sign_id);

        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                val message =
                    "We need your consent for the following permissions in order to use the offline call function properly"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }).request(RequestCallback { allGranted, grantedList, deniedList -> })


        lifecycleScope.launch {
            val userId = Helpers.getUserId(applicationContext)
            if (userId != null) {
                initCallInviteService(appID, appSign, userId, userId + "_name")
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(savePosition) {
                    1 -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                    else -> viewPager.setCurrentItem(1, true)
                }
                savePosition = 1
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.viewPager
        viewPager.adapter = ScreenSlidePagerAdapter(this@MainScreenActivity)
        val position: Int = intent.extras?.getInt("position", 1) ?: 1
        viewPager.setCurrentItem(position, false)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                savePosition = position
            }
        })
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)

        if (intent.getBooleanExtra("showPostFragment", false)) {
            viewPager.setCurrentItem(0, true)
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            }
        }
    }

    override fun onItemClick(position: Int, fragmentTag: String) {
        viewPager.setCurrentItem(position, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // when use minimize feature,it you swipe close this activity,call endCall()
        // to make sure call is ended and the float window is dismissed
        ZegoUIKitPrebuiltCallService.endCall()
    }
}
