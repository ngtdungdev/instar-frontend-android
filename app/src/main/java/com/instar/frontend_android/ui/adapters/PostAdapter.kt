package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.RecyclerViewItemAvatarBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.instar.frontend_android.ui.fragments.CommentBottomSheetDialogFragment
import com.instar.frontend_android.ui.fragments.ShareFragment
import com.instar.frontend_android.ui.fragments.SharePostBottomSheetDialogFragment

class PostAdapter(private val data: List<Post>, private val lifecycleScope: LifecycleCoroutineScope, private val user: User, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
    private val viewHolders: MutableList<PostViewHolder> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_newsfeed, parent, false)
        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        postService = ServiceBuilder.buildService(PostService::class.java, parent.context)
        return PostViewHolder(view, user, userService, postService, lifecycleScope, fragmentManager).also {
            viewHolders.add(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleLikePostRequest(data: Any?) {
        // Kiểm tra xem dữ liệu có hợp lệ không
        if (data is Map<*, *>) {
            // Lấy thông tin về bài đăng từ dữ liệu
            val post = data["post"] as? Post
            // Lấy thông tin về người gửi "like" từ dữ liệu
            val sender = data["sender"] as? User
            // Kiểm tra xem post và sender có tồn tại không
            if (post != null && sender != null) {
                // Cập nhật trạng thái "like" của bài đăng
                post.likes.add(sender.id)
                // Thông báo cập nhật dữ liệu cho adapter
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        // Assuming Post class has a property 'id' that uniquely identifies each post
        return data[position].id.hashCode().toLong()
    }

    class PostViewHolder(view: View, user:User, userService: UserService, postService: PostService, private val lifecycleScope: LifecycleCoroutineScope, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(view) {
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
        private val user: User = user
        private val gson = Gson() // Initialize Gson

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
        private var isSaved = false

        suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
            return withContext(Dispatchers.IO) {
                userService.getUser(userId).awaitResponse()
            }
        }

        // Bind data to views
        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(post: Post) {
            author.text = "Anonymous"
            val avatarBinding = RecyclerViewItemAvatarBinding.bind(avatar)

            if (user.stories.isEmpty()) {
                avatarBinding.hasStory.visibility = View.GONE
            } else {
                avatarBinding.hasStory.visibility = View.VISIBLE
            }

            val context = itemView.context
            val likes: MutableList<String> = post.likes.toMutableList()

            val id = user.id

            isSaved = user.savedPosts.contains(post.id)

            isLiked = post.likes.contains(id)

            if (isLiked) {
                heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
            } else {
                heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
            }

            if (isSaved) {
                saved.setBackgroundResource(R.drawable.icon_bookmark_black)
            } else {
                saved.setBackgroundResource(R.drawable.icon_bookmark_white)
            }

            lifecycleScope.launch {
                try {
                    val response = getUserData(post.userId)
                    withContext(Dispatchers.Main) {
                        author.text = response.data?.user?.username ?: "Anonymous"
                        // Load avatar image using Glide
                        Glide.with(context)
                            .load(response.data?.user?.profilePicture?.url)
                            .placeholder(R.drawable.default_image) // Placeholder image
                            .error(R.drawable.default_image) // Image to display if load fails
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

                            val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                                override fun onDoubleTap(e: MotionEvent): Boolean {
                                    if (id != null && !isLiked) {
                                        postService.likePost(id!!, post.id).handleResponse(
                                            onSuccess = { response ->
                                                likes.add(id)
                                                isLiked = true
                                                heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
                                                likeTotal.text = likes.size.toString() + " lượt thích"
                                            },
                                            onError = { error ->
                                                // Handle error
                                                val message = error.message
                                                Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                                            }
                                        )
                                    }
                                    return true
                                }
                            })
                            val carouselAdapter = CarouselAdapter(context,mediaList, gestureDetector)
                            carousel.adapter = carouselAdapter
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

            subName.text = feelingText
            likeTotal.text = likes.size.toString() + " lượt thích"
            content.text = post.desc

            if (post.comments.isEmpty()) {
                commentTotal.visibility = View.GONE
            } else {
                commentTotal.text = "Xem tất cả ${post.comments.size} bình luận"
            }

            createdAt.text = Helpers.convertToTimeAgo(post.createdAt)

            // Set OnClickListener for heart ImageButton
            heart.setOnClickListener {
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

            // Set OnClickListener for comment ImageButton
            comment.setOnClickListener {
                val existingFragment = fragmentManager.findFragmentByTag(CommentBottomSheetDialogFragment.TAG)
                if (existingFragment == null) {
                    // Fragment chưa được thêm vào trước đó, hãy tạo và hiển thị nó
                    val fragment = CommentBottomSheetDialogFragment.newInstance()
                    fragment.setPost(post)
                    fragment.setUserId(user)
                    fragment.show(fragmentManager, CommentBottomSheetDialogFragment.TAG)
                } else {
                    // Fragment đã tồn tại, không cần thêm mới
                    Log.d("PostAdapter", "CommentBottomSheetDialogFragment already added")
                }
            }

            // Set OnClickListener for share ImageButton
            share.setOnClickListener {
                val fragment = ShareFragment()
                fragment.show(fragmentManager, "ShareFragment - " + post.id)
            }

            saved.setOnClickListener {
                postService.savePost(id, post.id).handleResponse(
                    onSuccess = { response ->
                        if (isSaved) {
                            saved.setBackgroundResource(R.drawable.icon_bookmark_white)
                        } else {
                            saved.setBackgroundResource(R.drawable.icon_bookmark_black)
                        }
                        isSaved = !isSaved
                    },
                    onError = { error ->
                        // Handle error
                        val message = error.message;
                        Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                    }
                )
            }

            share.setOnClickListener {
                SharePostBottomSheetDialogFragment().show(fragmentManager , SharePostBottomSheetDialogFragment.TAG)
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
