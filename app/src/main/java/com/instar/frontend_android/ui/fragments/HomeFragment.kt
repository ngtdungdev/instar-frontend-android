package com.instar.frontend_android.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.FragmentHomeBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.activities.LoginOtherActivity
import com.instar.frontend_android.ui.activities.NotificationActivity
import com.instar.frontend_android.ui.activities.ProfileActivity
import com.instar.frontend_android.ui.activities.SearchActivity
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.adapters.PostAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.OnFragmentClickListener
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.StoryService
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import com.instar.frontend_android.ui.utils.PostAdapterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var imageList: ArrayList<Images> = ArrayList<Images>()
    private lateinit var newsFollowAdapter: NewsFollowAdapter
    private lateinit var avatarRecyclerView: RecyclerView

    private lateinit var feedList: MutableList<PostAdapterType>
    private lateinit var postAdapter: PostAdapter
    private lateinit var feedsRecyclerView: RecyclerView

    private lateinit var btnMessage: ImageView
    private lateinit var binding: FragmentHomeBinding

    private lateinit var userService: UserService
    private lateinit var authService: AuthService
    private lateinit var postService: PostService
    private lateinit var storyService: StoryService
    private lateinit var userResponse: UserResponse
    private lateinit var btnPostUp: ImageButton
    private lateinit var btnPersonal: View
    private lateinit var url: ImageView
    private lateinit var iconHeart: ImageView
    private lateinit var layout: View
    private lateinit var btnHome: ImageView
    private lateinit var btnSearch:ImageView
    private lateinit var btnReel:ImageView

    private var listener: OnFragmentClickListener? = null
    private fun fragmentClick(position: Int) {
        listener?.onItemClick(position, "HomeFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentClickListener) {
            listener = context
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        userService = ServiceBuilder.buildService(UserService::class.java, requireContext())
        authService = ServiceBuilder.buildService(AuthService::class.java, requireContext())
        postService = ServiceBuilder.buildService(PostService::class.java, requireContext())
        storyService = ServiceBuilder.buildService(StoryService::class.java, requireContext())

        avatarRecyclerView = binding.stories
        layout = binding.gridLayout
        feedsRecyclerView = binding.newsfeed
        btnMessage = binding.iconMessenger
        btnPostUp = binding.btnPostUp
        btnPersonal = binding.btnPersonal
        iconHeart = binding.iconHeart
        btnHome = binding.btnHome
        btnReel = binding.btnReel
        btnSearch = binding.btnSearch
        url = binding.url
        initView()
        authService.profile().handleResponse(
            onSuccess = { response ->
                userResponse = response.data!!
                val avatarUrl = response.data.user?.profilePicture?.url
                val idUser = response.data.user?.id
                CoroutineScope(Dispatchers.Main).launch {
                    imageList = getStorys()
                    val image0 = Images(
                        Images.TYPE_PERSONAL_AVATAR,
                        idUser.toString(),
                        "Tin của bạn",
                        avatarUrl
                    )

                    println(imageList.size)

                    imageList.add(0, image0)
                    newsFollowAdapter = NewsFollowAdapter(context,imageList)
                    avatarRecyclerView.adapter = newsFollowAdapter
                }

                Glide.with(requireContext())
                    .load(response.data?.user?.profilePicture?.url)
                    .placeholder(R.drawable.default_image) // Placeholder image
                    .error(R.drawable.default_image) // Image to display if load fails
                    .into(url)

                lifecycleScope.launch {
                    loadRecyclerView() // Call the suspend function within the coroutine
                }
            },
            onError = { error ->
                // Handle error
                val message = error.message;
                Log.e("ServiceBuilder", "Error: $message - ${error.status}")

                ServiceBuilder.setRefreshToken(requireContext(), null)
                ServiceBuilder.setAccessToken(requireContext(), null)

                val intent = Intent(context, LoginOtherActivity::class.java)
                startActivity(intent)
            })
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        btnPersonal.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java);
            startActivity(intent)
        }
        btnSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java);
            startActivity(intent)
        }
        btnMessage.setOnClickListener {
            if (listener != null) {
                fragmentClick(2)
            }
        }
        btnPostUp.setOnClickListener {
            if (listener != null) {
                fragmentClick(0)
            }
        }
        iconHeart.setOnClickListener {
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putExtra("user", userResponse.user)
            startActivity(intent)
        }
        widthLayout = (getScreenWidth(requireContext()) - dpToPx(30 * 4 + 10 * 2 + 37)) / 4
        setMargin(btnSearch)
        setMargin(btnPersonal)
        setMargin(btnReel)
        setMargin(btnPostUp)
    }
    private var widthLayout: Int? = null

    @RequiresApi(Build.VERSION_CODES.R)
    fun setMargin(view: View) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.leftMargin = widthLayout!!
        view.layoutParams = layoutParams
    }

    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = wm.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
    }

    private suspend fun loadRecyclerView() {
        val postList = getPosts()
        val suggestPostList = getSuggestedPosts()
        feedList = mutableListOf()

        for (post in postList) {
            feedList.add(PostAdapterType(post, null, null, PostAdapterType.VIEW_TYPE_DEFAULT))
        }

        if (suggestPostList.size > 0) {
            feedList.add(PostAdapterType(null, "Gợi ý bài viết cho bạn", null, PostAdapterType.VIEW_TYPE_SUGGEST))

            for (suggestPost in suggestPostList) {
                feedList.add(PostAdapterType(suggestPost, null, null, PostAdapterType.VIEW_TYPE_DEFAULT))
            }
        }

        postAdapter = userResponse.user?.let { PostAdapter(feedList, lifecycleScope, it, requireActivity().supportFragmentManager) }!!
        postAdapter.setOnRemoveChangedListener(object : PostAdapter.OnRemoveChangedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onRemoveChanged(position: Int) {
                // Gọi lại phương thức loadComments() trong PostAdapter khi danh sách comment thay đổi
                feedList.removeAt(position)
                val clonedList = feedList.toMutableList()
                feedList.clear()
                feedList.addAll(clonedList)
                postAdapter.notifyItemRemoved(position)
                postAdapter.notifyDataSetChanged()
            }
        })
        feedsRecyclerView.layoutManager = LinearLayoutManager(context)
        feedsRecyclerView.adapter = postAdapter
    }
    private suspend fun getStorys(): ArrayList<Images> {
        val imageList = ArrayList<Images>()

        val context = requireContext()
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", null)
        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")
            val response = try {
                val response = storyService.getStoriesTimelineByUserId(id).awaitResponse()
                response
            } catch (error: Throwable) {
                error.printStackTrace()
                null
            }
            if (response != null) {
                val storyResponse = response.data?.timelineStories ?: ArrayList()

                val userIdSet = HashSet<String>()

                for (story in storyResponse) {
                    for (str in story.stories!!) {
                        if (!userIdSet.contains(story.userId)) {
                            val userInfoResponse = getUserData(story.userId.toString())
                            val avatarUrl = userInfoResponse.data?.user?.profilePicture?.url
                            val userName = userInfoResponse.data?.user?.username
                            imageList.add(
                                Images(
                                    Images.TYPE_FRIEND_AVATAR,
                                    story.userId.toString(),
                                    userName.toString(),
                                    avatarUrl
                                )
                            )
                            userIdSet.add(story.userId.toString())
                        }
                    }
                }
            } else {
                // Handle the case where the response is null
                Log.e("Error", "Failed to get timeline stories")
            }
        }
        return imageList
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }


    private suspend fun getPosts(): ArrayList<Post> {
        var postsList = ArrayList<Post>()

        val context = requireContext()

        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")
            val response = try {
                val response = postService.getTimelinePosts(id).awaitResponse()
                response
            } catch (error: Throwable) {
                // Handle error
                error.printStackTrace() // Print stack trace for debugging purposes
                null // Return null to indicate that an error occurred
            }
            if (response != null) {
                postsList = response.data?.timelinePosts ?: ArrayList()

            } else {
                // Handle the case where the response is null
                Log.e("Error", "Failed to get timeline posts")
            }
        }

        return postsList
    }

    private suspend fun getSuggestedPosts(): ArrayList<Post> {
        var postsList = ArrayList<Post>()

        val context = requireContext()

        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            val id = decodedTokenJson.getString("id")
            val response = try {
                val response = postService.getTimelinePostsForYou(id).awaitResponse()
                response
            } catch (error: Throwable) {
                // Handle error
                error.printStackTrace() // Print stack trace for debugging purposes
                null // Return null to indicate that an error occurred
            }
            if (response != null) {
                postsList = response.data?.timelineYourPosts ?: ArrayList()

            } else {
                // Handle the case where the response is null
                Log.e("Error", "Failed to get timeline posts")
            }
        }

        return postsList
    }
}