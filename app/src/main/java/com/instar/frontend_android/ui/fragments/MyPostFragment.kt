package com.instar.frontend_android.ui.fragments

import CustomAdapter
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.PostResponse
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyPostFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var postService: PostService
    private lateinit var myPostList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postService = ServiceBuilder.buildService(PostService::class.java, requireContext())
        val linearViewNoItems: LinearLayout = view.findViewById(com.instar.frontend_android.R.id.linearNoItems)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewPost) // Sử dụng id recyclerViewPost
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")

            // gọi để lấy bài viết và saved bài viết
            lifecycleScope.launch {
                try {
                    val response1 = getMyPostsData(id)
                    myPostList = response1.data?.posts!!

                    val adapter = CustomAdapter(requireContext(), myPostList)
                    recyclerView.adapter = adapter
                    if (recyclerView.getAdapter() != null && recyclerView.getAdapter()?.getItemCount() == 0) {
                        linearViewNoItems.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE)
                    } else {
                        linearViewNoItems.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE)
                    }
                } catch (e: Exception) {
                    // Handle exceptions, e.g., log or show error to user
                    e.printStackTrace()
                }
            }
        }

    }

    private suspend fun getMyPostsData(userId: String): ApiResponse<PostResponse> {
        return withContext(Dispatchers.IO) {
            postService.getAllPostsByUserId(userId).awaitResponse()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
