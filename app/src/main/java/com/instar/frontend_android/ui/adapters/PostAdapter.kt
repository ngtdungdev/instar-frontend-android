package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
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
import com.instar.frontend_android.databinding.RecycleViewItemSuggestedPostBinding
import com.instar.frontend_android.databinding.RecyclerViewItemNewsfeedBinding
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.fragments.CommentBottomSheetDialogFragment
import com.instar.frontend_android.ui.fragments.SharePostBottomSheetDialogFragment
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.utils.PostAdapterType

class PostAdapter(private val data: MutableList<PostAdapterType>, private val lifecycleScope: LifecycleCoroutineScope, private val user: User, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
    private lateinit var notificationService: NotificationService
    private val viewHolders: MutableList<PostViewHolder> = mutableListOf()
    private lateinit var fcmNotificationService: FCMNotificationService

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutResId = when (viewType) {
            PostAdapterType.VIEW_TYPE_DEFAULT -> R.layout.recycler_view_item_newsfeed
            PostAdapterType.VIEW_TYPE_SUGGEST -> R.layout.recycle_view_item_suggested_post
            else -> throw IllegalArgumentException("Invalid view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)

        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        postService = ServiceBuilder.buildService(PostService::class.java, parent.context)
        notificationService = ServiceBuilder.buildService(NotificationService::class.java, parent.context)
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, parent.context)

        return PostViewHolder(view, user, userService, postService, fcmNotificationService, notificationService, lifecycleScope, fragmentManager).also {
            viewHolders.add(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type!!
    }

    override fun getItemId(position: Int): Long {
        return data[position].post?.id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = data[position]
        when (item.type) {
            PostAdapterType.VIEW_TYPE_SUGGEST -> holder.bindText(item.text!!)
            else -> holder.bind(item.post!!, position)
        }
    }

    class PostViewHolder(view: View, private val user:User, private val userService: UserService, private val postService: PostService, private val fcmNotificationService: FCMNotificationService, private val notificationService: NotificationService, private val lifecycleScope: LifecycleCoroutineScope, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(view) {
        private lateinit var postBinding: RecyclerViewItemNewsfeedBinding

        private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback

        private val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            setMargins(8,0,8,0)
        }

        private var isLiked = false
        private var isSaved = false

        private lateinit var suggestedPostBinding: RecycleViewItemSuggestedPostBinding

        suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
            return withContext(Dispatchers.IO) {
                userService.getUser(userId).awaitResponse()
            }
        }

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "LogNotTimber")
        fun bind(post: Post, position: Int) {
            if (!::postBinding.isInitialized) {
                postBinding = RecyclerViewItemNewsfeedBinding.bind(itemView)
            }

            postBinding.author.text = "Anonymous"

            val avatarBinding = RecyclerViewItemAvatarBinding.bind(itemView.findViewById(R.id.avatar))


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
                postBinding.heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
            } else {
                postBinding.heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
            }

            if (isSaved) {
                postBinding.saved.setBackgroundResource(R.drawable.icon_bookmark_black)
            } else {
                postBinding.saved.setBackgroundResource(R.drawable.icon_bookmark_white)
            }

            lifecycleScope.launch {
                try {
                    val response = getUserData(post.userId)
                    withContext(Dispatchers.Main) {
                        postBinding.author.text = response.data?.user?.username ?: "Anonymous"
                        Glide.with(context)
                            .load(response.data?.user?.profilePicture?.url)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(avatarBinding.url)

                        val urls: List<String> = post.fileUploads.mapNotNull { it.url }

                        if (urls.isEmpty() || post.fileUploads.isEmpty()) {
                            postBinding.carousel.visibility = View.GONE
                            postBinding.carouselParent.visibility = View.GONE
                            postBinding.carouselParent.layoutParams.height = 0
                            postBinding.carouselDot.visibility = View.GONE
                            postBinding.carouselParent.requestLayout()
                        } else {
                            if (urls.size > 1) {
                                postBinding.carouselDot.visibility = View.VISIBLE
                                postBinding.carouselDot.removeAllViews()
                                val dotsImage = Array(urls.size) { ImageView(context) }
                                dotsImage.forEach {
                                    it.setImageResource(R.drawable.non_active_dot)
                                    postBinding.carouselDot.addView(it, params)
                                }
                                postBinding.imageCounter.text = "1/${dotsImage.size}"
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
                                        postBinding.imageCounter.text = "${position + 1}/${dotsImage.size}"
                                        super.onPageSelected(position)
                                    }
                                }

                                postBinding.carousel.registerOnPageChangeCallback(pageChangeListener)
                            } else {
                                postBinding.carouselDot.visibility = View.GONE
                                postBinding.imageCounter.visibility = View.GONE
                            }

                            val mediaList = urls.map { url ->
                                url to Helpers.getMediaType(url)
                            }

                            postBinding.carouselParent.visibility = View.VISIBLE
                            postBinding.carousel?.visibility = View.VISIBLE

                            val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                                override fun onDoubleTap(e: MotionEvent): Boolean {
                                    if (id != null && !isLiked) {
                                        postService.likePost(id!!, post.id).handleResponse(
                                            onSuccess = { response ->
                                                likes.add(id)
                                                isLiked = true
                                                postBinding.heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
                                                postBinding.likeTotal.text = "${likes.size} lượt thích"


                                                val notificationRequest = NotificationRequest(post.id, null, id, post.userId, "${user.username} đã thích bài viết của bạn", "like-post")

                                                notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                                    onSuccess = { println("Successfully sent the user notification.") },
                                                    onError = { println("Error while sending user notification.") }
                                                )

                                                fcmNotificationService.sendLikePostNotification(notificationRequest).handleResponse(
                                                    onSuccess = { println("Successfully sent the user notification.") },
                                                    onError = {
                                                        println(it)
                                                        println("Error while sending user notification.")
                                                    }
                                                )
                                            },
                                            onError = { error ->
                                                val message = error.message
                                                Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                                            }
                                        )
                                    }
                                    return true
                                }
                            })
                            val carouselAdapter = CarouselAdapter(context, mediaList, gestureDetector)
                            postBinding.carousel.adapter = carouselAdapter
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PostAdapter", "Error: ${e.message}")
                }
            }

            val feelingText = if (post.feeling.isNotEmpty()) {
                "Đang cảm thấy ${post.feeling}"
            } else {
                ""
            }

            postBinding.subName.text = feelingText
            postBinding.likeTotal.text = "${likes.size} lượt thích"
            postBinding.content.text = post.desc

            if (post.comments.isEmpty()) {
                postBinding.commentTotal.visibility = View.GONE
            } else {
                postBinding.commentTotal.text = "Xem tất cả ${post.comments.size} bình luận"
            }

            postBinding.createdAt.text = Helpers.convertToTimeAgo(post.createdAt)

            postBinding.heart.setOnClickListener {
                postService.likePost(id, post.id).handleResponse(
                    onSuccess = { response ->
                        if (isLiked) {
                            likes.remove(id)
                            postBinding.heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
                        } else {
                            likes.add(id)
                            postBinding.heart.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)

                            val notificationRequest = NotificationRequest(post.id, null, id, post.userId, "${user.username} đã thích bài viết của bạn", "like-post")

                            notificationService.createNotification(post.userId, notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the user notification.") },
                                onError = { println("Error while sending user notification.") }
                            )

                            fcmNotificationService.sendLikePostNotification(notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the user notification.") },
                                onError = { println("Error while sending user notification.") }
                            )
                        }
                        isLiked = !isLiked
                        postBinding.likeTotal.text = "${likes.size} lượt thích"
                    },
                    onError = { error ->
                        val message = error.message
                        Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                    }
                )
            }

            postBinding.comment.setOnClickListener {
                val existingFragment = fragmentManager.findFragmentByTag(CommentBottomSheetDialogFragment.TAG)
                if (existingFragment == null) {
                    val fragment = CommentBottomSheetDialogFragment.newInstance()
                    fragment.setPost(post)
                    fragment.setUserId(user)
                    fragment.setOnCommentChangedListener(object : CommentBottomSheetDialogFragment.OnCommentChangedListener {
                        override fun onCommentChanged(commentWithReply: MutableList<CommentWithReply>) {
                            // Gọi lại phương thức loadComments() trong PostAdapter khi danh sách comment thay đổi
                            val newComments = mutableListOf<Comment>();

                            commentWithReply.forEach {
                                newComments.add(it.comment)

                                it.replies.forEach {it2 ->
                                    newComments.add(it2)
                                }
                            }

                            post.comments.clear()
                            post.comments.addAll(newComments)

                            postBinding.commentTotal.text = "Xem tất cả ${post.comments.size} bình luận"
                        }
                    })
                    fragment.show(fragmentManager, CommentBottomSheetDialogFragment.TAG)
                } else {
                    Log.d("PostAdapter", "CommentBottomSheetDialogFragment already added")
                }
            }

            postBinding.share.setOnClickListener {
                SharePostBottomSheetDialogFragment(post).show(fragmentManager, SharePostBottomSheetDialogFragment.TAG)
            }

            postBinding.saved.setOnClickListener {
                postService.savePost(id, post.id).handleResponse(
                    onSuccess = { response ->
                        if (isSaved) {
                            postBinding.saved.setBackgroundResource(R.drawable.icon_bookmark_white)
                        } else {
                            postBinding.saved.setBackgroundResource(R.drawable.icon_bookmark_black)
                        }
                        isSaved = !isSaved
                    },
                    onError = { error ->
                        val message = error.message
                        Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                    }
                )
            }

            postBinding.viewMore.setOnClickListener {

            }

            val userId = Helpers.getUserId(context);

            if (userId.equals(post.userId)) {
                postBinding.action.visibility = View.VISIBLE;
            } else {
                postBinding.action.visibility = View.GONE
            }

            postBinding.action.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            lifecycleScope.launch {
                                if (userId != null) {
                                    withContext(Dispatchers.IO) {
                                        postService.deletePost(userId, post.id).awaitResponse()
                                    }

                                    (itemView.context as? RecyclerView)?.adapter?.let { adapter ->
                                        if (adapter is PostAdapter) {
                                                adapter.removeItem(position)
                                        }
                                    }
                                };
                            }
                            true
                        }
                        R.id.action_hide -> {
                            // Todo: sẽ làm thêm trong tương lai xa
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.post_actions)
                popupMenu.show()
            }
        }



        fun clearViewPagerCallback() {
            postBinding.carousel.unregisterOnPageChangeCallback(pageChangeListener)
        }

        fun bindText(text: String) {
            if (!::suggestedPostBinding.isInitialized) {
                suggestedPostBinding = RecycleViewItemSuggestedPostBinding.bind(itemView)
            }
            suggestedPostBinding.title.text = text
        }
    }

    override fun getItemCount(): Int = data.size

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    private fun clearPageChangeListeners() {
        for (holder in viewHolders) {
            holder.clearViewPagerCallback()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        clearPageChangeListeners()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}
