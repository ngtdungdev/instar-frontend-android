package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityLoginOtherBinding
import com.instar.frontend_android.databinding.ActivityNotificationBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.ui.DTO.Notification
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.DirectMessageAdapter
import com.instar.frontend_android.ui.adapters.NotificationAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class NotificationActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    private lateinit var btnBack: ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var notificationService: NotificationService;
    private lateinit var notificationAdapter: NotificationAdapter;

    private lateinit var notificationList: MutableList<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationService = ServiceBuilder.buildService(NotificationService::class.java, this)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBack = binding.btnBack
        recyclerView = binding.recyclerViewNotification

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("accessToken", null)

            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                val id = decodedTokenJson.getString("id")
                val response = notificationService.getNotifications(id).awaitResponse()
                notificationList = response.data?.notificationList!!;
            }
        }

        val user:User? = intent.getSerializableExtra("user") as? User

        notificationAdapter =
            user?.let { NotificationAdapter(this, notificationList, it, lifecycleScope) }!!
        recyclerView.adapter = notificationAdapter
    }
}