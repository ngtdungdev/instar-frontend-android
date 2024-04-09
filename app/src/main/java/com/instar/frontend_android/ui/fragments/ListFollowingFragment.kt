package com.instar.frontend_android.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.instar.frontend_android.ui.adapters.CustomFollowingAdapter
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ListFollowingFragment(private val userId: String) : Fragment() {
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
        return inflater.inflate(R.layout.fragment_list_following, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerFollowing = view.findViewById<RecyclerView>(R.id.recyclerFollowing)
        val linearViewNoItems: LinearLayout = view.findViewById(R.id.linearNoItems)
        recyclerFollowing.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            try {
                val response: ApiResponse<UserResponse> = getUserData(userId)
                val user = response.data?.user
                val dataList = user?.followings

                recyclerFollowing.adapter =
                    dataList?.let { CustomFollowingAdapter(requireContext(), it, lifecycleScope) }

                if (recyclerFollowing.adapter != null && recyclerFollowing.adapter?.itemCount == 0) {
                    linearViewNoItems.visibility = View.VISIBLE
                    recyclerFollowing.visibility = View.GONE
                } else {
                    linearViewNoItems.visibility = View.GONE
                    recyclerFollowing.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., log or show error to user
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
        fun newInstance(userId: String): ListFollowingFragment {
            return ListFollowingFragment(userId)
        }
    }
}