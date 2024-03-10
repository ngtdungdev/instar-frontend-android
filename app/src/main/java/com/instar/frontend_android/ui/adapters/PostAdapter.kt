package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.RecyclerViewItemAvatarBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.activities.LoginOtherActivity
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(private val data: List<Post>, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
    private lateinit var userId: String
    private val viewHolders: MutableList<PostViewHolder> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_newsfeed, parent, false)
        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        postService = ServiceBuilder.buildService(PostService::class.java, parent.context)
        return PostViewHolder(view, userService, postService, lifecycleScope).also {
            viewHolders.add(it)
        }
    }

    override fun getItemId(position: Int): Long {
        // Assuming Post class has a property 'id' that uniquely identifies each post
        return data[position].id.hashCode().toLong()
    }

    class PostViewHolder(view: View, userService: UserService, postService: PostService, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.ViewHolder(view) {
        private val avatar: View = view.findViewById(R.id.avatar)
        private val author: TextView = view.findViewById(R.id.author)
        private val subName: TextView = view.findViewById(R.id.subName)
        private val likeTotal: TextView = view.findViewById(R.id.likeTotal)
        private val content: TextView = view.findViewById(R.id.content)
        private val commentTotal: TextView = view.findViewById(R.id.commentTotal)
        private val createdAt: TextView = view.findViewById(R.id.createdAt)
        private val viewMore: TextView = view.findViewById(R.id.viewMore)
        private val heart: ImageButton = view.findViewById(R.id.heart)
        private val comment: ImageButton = view.findViewById(R.id.comment)
        private val share: ImageButton = view.findViewById(R.id.share)
        private val saved: ImageButton = view.findViewById(R.id.saved)
        private val imageCounter: TextView = view.findViewById(R.id.imageCounter)

        private val userService: UserService = userService
        private val postService: PostService = postService

        private val carousel: ViewPager2 = view.findViewById(R.id.carousel)
        private val carouselParent: RelativeLayout = view.findViewById(R.id.carouselParent)
        private val carouselDot: LinearLayout = view.findViewById(R.id.carouselDot)

        private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
        private val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            setMargins(8,0,8,0)
        }

        private var isLiked = false
        private val DOUBLE_CLICK_DELAY_MS = 300
        private var lastClickTime: Long = 0

        suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
            return withContext(Dispatchers.IO) {
                userService.getUser(userId).awaitResponse()
            }
        }

        // Bind data to views
        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(post: Post) {
            author.text = "Anonymous"
            val context = itemView.context

            lifecycleScope.launch {
                try {
                    val response = getUserData(post.userId)
                    if (response != null) {
                        author.text = response.data.user?.username ?: "Anonymous"
                        val avatarBinding = RecyclerViewItemAvatarBinding.bind(avatar)

                        Glide.with(context)
                            .load(response.data.user?.profilePicture?.url)
                            .into(avatarBinding.url)

                        val urls: List<String> = post.fileUploads.mapNotNull { it.url }

                        if (urls.isEmpty() || post.fileUploads.isEmpty()) {
                            carousel.visibility = View.GONE
                            carouselParent.visibility = View.GONE
                            carouselParent.layoutParams.height = 0
                            carouselDot.visibility = View.GONE
                            carouselParent.requestLayout()
                        } else {
                            if (urls.size > 1) {
                                carouselDot.visibility = View.VISIBLE

                                carouselDot.removeAllViews()

                                val dotsImage = Array(urls.size) {ImageView(context)}

                                dotsImage.forEach {
                                    it.setImageResource(R.drawable.non_active_dot)
                                    carouselDot.addView(it, params)
                                }

                                imageCounter.text = "1/${dotsImage.size}"
                                dotsImage[0].setImageResource(R.drawable.active_dot)

                                pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        dotsImage.mapIndexed { index, imageView ->
                                            if (position == index) {
                                                imageView.setImageResource(R.drawable.active_dot)
                                            } else {
                                                imageView.setImageResource(R.drawable.non_active_dot)
                                            }
                                        }
                                        imageCounter.text = "${position + 1}/${dotsImage.size}"
                                        super.onPageSelected(position)
                                    }
                                }

                                carousel.registerOnPageChangeCallback(pageChangeListener)
                            } else {
                                carouselDot.visibility = View.GONE
                                imageCounter.visibility = View.GONE
                            }

                            val mediaList = urls.map { url ->
                                url to Helpers.getMediaType(url)
                            }

                            carouselParent.visibility = View.VISIBLE
                            carousel.visibility = View.VISIBLE
                            val carouselAdapter = CarouselAdapter(mediaList)
                            carousel.adapter = carouselAdapter
                        }

                        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

                        val accessToken = sharedPreferences.getString("accessToken", null)

                        if (accessToken != null) {
                            val decodedTokenJson = Helpers.decodeJwt(accessToken)
                            val id = decodedTokenJson.getString("id")
                            isLiked = post.likes.contains(id)

                            if (isLiked) {
                                heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
                            } else {
                                heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                    println("Error: ${e.message}")
                }
            }

            val feelingText = if (post.feeling.isNotEmpty()) {
                "Đang cảm thấy ${post.feeling}"
            } else {
                ""
            }
            val likes: MutableList<String> = post.likes.toMutableList()

            subName.text = feelingText
            likeTotal.text = likes.size.toString() + " lượt thích"
            content.text = post.desc
            commentTotal.text = "Xem tất cả ${post.comments.size} bình luận"
            createdAt.text = Helpers.convertToTimeAgo(post.createdAt)
            val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

            val accessToken = sharedPreferences.getString("accessToken", null)
            var id: String? = null
            if (accessToken != null) {
                val decodedTokenJson = Helpers.decodeJwt(accessToken)
                id = decodedTokenJson.getString("id")
            }

            // Set OnClickListener for heart ImageButton
            heart.setOnClickListener {
                if (id == null) return@setOnClickListener

                postService.likePost(id, post.id).handleResponse(
                    onSuccess = { response ->
                        if (isLiked) {
                            likes.remove(id)
                            heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
                        } else {
                            likes.add(id)
                            heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
                        }
                        isLiked = !isLiked
                        likeTotal.text = likes.size.toString() + " lượt thích"
                    },
                    onError = { error ->
                        // Handle error
                        val message = error.message;
                        Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                    }
                )
            }

//            carouselParent.setOnTouchListener { _, event ->
//                if (event.action == MotionEvent.ACTION_UP) {
//                    val clickTime = System.currentTimeMillis()
//                    if (clickTime - lastClickTime < DOUBLE_CLICK_DELAY_MS) {
//                        // Double-click detected
//                        Log.d("GestureDetector", "Double click detected")
//                        if (id != null && !isLiked) {
//                            postService.likePost(id, post.id).handleResponse(
//                                onSuccess = { response ->
//                                    likes.add(id)
//                                    isLiked = true
//                                    heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
//                                    likeTotal.text = likes.size.toString() + " lượt thích"
//                                },
//                                onError = { error ->
//                                    // Handle error
//                                    val message = error.message
//                                    Log.e("ServiceBuilder", "Error: $message - ${error.status}")
//                                }
//                            )
//                        }
//                    }
//                    lastClickTime = clickTime
//                }
//                true
//            }

            // Set OnClickListener for comment ImageButton
            comment.setOnClickListener {
                // Handle comment button click event
            }

            // Set OnClickListener for share ImageButton
            share.setOnClickListener {
                // Handle share button click event
            }

            saved.setOnClickListener {

            }

            viewMore.setOnClickListener {

            }
        }

        // Method to clear resources associated with ViewPager2
        fun clearViewPagerCallback() {
            carousel.unregisterOnPageChangeCallback(pageChangeListener)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    fun clearPageChangeListeners() {
        for (holder in viewHolders) {
            holder.clearViewPagerCallback()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        clearPageChangeListeners()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}

