package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityNotificationBinding
import com.instar.frontend_android.ui.DTO.Notification
import com.instar.frontend_android.ui.adapters.NotificationAdapter
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class NotificationActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    private lateinit var btnBack: ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var notificationService: NotificationService;
    private lateinit var userService: UserService;
    private lateinit var notificationAdapter: NotificationAdapter;

    private var notificationList: MutableList<Notification> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationService = ServiceBuilder.buildService(NotificationService::class.java, this)
        userService = ServiceBuilder.buildService(UserService::class.java, this)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBack = binding.btnBack

        btnBack.setOnClickListener {
            onBackPressed();
        }

        recyclerView = binding.recyclerViewNotification

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        notificationAdapter = NotificationAdapter(applicationContext, mutableListOf(), null, lifecycleScope)
        recyclerView.adapter = notificationAdapter

        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("accessToken", null)

            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")
                val response = notificationService.getNotifications(id).awaitResponse()
                val userResponse = userService.getUser(id).awaitResponse()
                notificationList = response.data?.notificationList!!
                val user = userResponse.data?.user!!

                notificationAdapter = NotificationAdapter(applicationContext, notificationList, user, lifecycleScope)
                recyclerView.adapter = notificationAdapter
            }
        }
    }
}