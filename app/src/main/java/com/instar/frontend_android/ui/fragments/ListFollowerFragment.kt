package com.instar.frontend_android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.adapters.CustomFollowerAdapter
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFollowerFragment(private val userId: String) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerFollower = view.findViewById<RecyclerView>(R.id.recyclerFollower)
        val linearViewNoItems: LinearLayout = view.findViewById(R.id.linearNoItems)
        recyclerFollower.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            try {
                val response:ApiResponse<UserResponse> = getUserData(userId)
                val user = response.data?.user
                val dataList = user?.followers
                recyclerFollower.adapter =
                    dataList?.let { CustomFollowerAdapter(requireContext(), it,lifecycleScope) }
                if (recyclerFollower.adapter != null && recyclerFollower.adapter?.itemCount == 0) {
                    linearViewNoItems.visibility = View.VISIBLE
                    recyclerFollower.visibility = View.GONE
                } else {
                    linearViewNoItems.visibility = View.GONE
                    recyclerFollower.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): ListFollowerFragment  {
            return ListFollowerFragment (userId)
        }
    }
}
