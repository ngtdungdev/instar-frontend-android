package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.RecyclerViewItemAvatarBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(private val data: List<Post>, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private lateinit var userService: UserService
    private val viewHolders: MutableList<PostViewHolder> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_newsfeed, parent, false)
        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        return PostViewHolder(view, userService, lifecycleScope).also {
            viewHolders.add(it)
        }
    }

    override fun getItemId(position: Int): Long {
        // Assuming Post class has a property 'id' that uniquely identifies each post
        return data[position].id.hashCode().toLong()
    }

    class PostViewHolder(view: View, userService: UserService, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.ViewHolder(view) {
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

        private val userService: UserService = userService

        private val carousel: ViewPager2 = view.findViewById(R.id.carousel)
        private val carouselDot: LinearLayout = view.findViewById(R.id.carouselDot)

        private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
        private val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            setMargins(8,0,8,0)
        }

        suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
            return withContext(Dispatchers.IO) {
                userService.getUser(userId).awaitResponse()
            }
        }

        // Bind data to views
        fun bind(post: Post) {
            author.text = "anonymous"
            val context = itemView.context

            lifecycleScope.launch {
                try {
                    val response = getUserData(post.userId)
                    if (response != null) {
                        author.text = response.data.user?.username ?: "anonymous"
                        val avatarBinding = RecyclerViewItemAvatarBinding.bind(avatar)

                        Glide.with(context)
                            .load(response.data.user?.profilePicture?.url)
                            .into(avatarBinding.url)

                        val urls: List<String> = post.fileUploads.mapNotNull { it.url }

                        if (urls.size <= 0) {
                            carousel.visibility = View.GONE
                            carouselDot.visibility = View.GONE
                        } else {
                            if (urls.size > 1) {
                                carouselDot.visibility = View.VISIBLE

                                carouselDot.removeAllViews()

                                val dotsImage = Array(urls.size) {ImageView(context)}

                                dotsImage.forEach {
                                    it.setImageResource(R.drawable.non_active_dot)
                                    carouselDot.addView(it, params)
                                }

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
                                        super.onPageSelected(position)
                                    }
                                }

                                carousel.registerOnPageChangeCallback(pageChangeListener)
                            } else {
                                carouselDot.visibility = View.GONE
                            }

                            carousel.visibility = View.VISIBLE
                            val carouselAdapter = CarouselAdapter(urls)
                            carousel.adapter = carouselAdapter
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                    println("Error: ${e.message}")
                }
            }

            subName.text = ""
            likeTotal.text = post.likes.size.toString() + " lượt thích"
            content.text = post.desc
            commentTotal.text = "Xem tất cả ${post.comments.size} bình luận"
            createdAt.text = Helpers.convertToTimeAgo(post.createdAt)

            // Set OnClickListener for heart ImageButton
            heart.setOnClickListener {
                // Handle heart button click event
            }

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

