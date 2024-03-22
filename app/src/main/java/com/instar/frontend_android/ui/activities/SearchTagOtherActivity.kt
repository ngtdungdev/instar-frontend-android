package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.enum.EnumUtils
import com.instar.frontend_android.interfaces.InterfaceUtils
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var btnRemove: ImageButton
    private lateinit var binding: ActivitySearchTagOtherBinding
    private lateinit var userService: UserService
    private lateinit var query: EditText

    private lateinit var userList: MutableList<User>
    private lateinit var tagList: MutableList<User>
    private lateinit var userAdapter: PostTagAdapter
    private lateinit var userRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTagOtherBinding.inflate(layoutInflater)
        userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
        query = binding.message;
        userRecyclerView = binding.searchTagRV
        setContentView(binding.root)
        btnRemove = binding.iconDelete

        // Nhận danh sách user từ Intent
        val list = intent.getSerializableExtra("userList") as? ArrayList<User>
        if (list != null) {
            tagList = list
        }

        // Thiết lập LinearLayoutManager cho RecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        initView();
    }

    fun setQuery() {
        query.hint = "Tìm kiếm người dùng"
    }

    fun initView() {
        val debounceHandler = Handler(Looper.getMainLooper())
        var debounceRunnable: Runnable? = null

        query.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let {
                    debounceHandler.removeCallbacks(it)
                }
                debounceRunnable = Runnable {
                    // Your logic to execute after debounce duration
                    if (s.toString().isEmpty()) {
                        setQuery()
                    } else {
                        search();
                    }
                }
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        query.setOnClickListener {
        }
    }

    fun search() {
        lifecycleScope.launch {
            val response = try {
                val response = userService.searchFollowingUser(query.text.toString()).awaitResponse()
                response
            } catch (error: Throwable) {
                error.printStackTrace()
                null
            }

            runOnUiThread {
                if (response != null && response.data.followingUsers != null) {
                    userList = response.data.followingUsers as MutableList<User>
                    updateUserList()
                } else {
                    Log.e("Error", "Failed to get following users")
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserList() {
        userAdapter = PostTagAdapter(this, userList, EnumUtils.PostTagType.TAG_SEARCH, object : InterfaceUtils.OnItemClickListener {
            override fun onItemClick(user: User) {
                // Kiểm tra xem user có trong tagList hay không
                val existingUser = tagList.find { it.id == user.id }

                // Nếu user không tồn tại trong tagList, thêm user vào danh sách
                if (existingUser == null) {
                    tagList.add(user)
                }

                val intent = Intent()
                intent.putExtra("tagList", ArrayList(tagList)) // Chuyển tagList qua Intent
                setResult(Activity.RESULT_OK, intent)
                finish() // Kết thúc activity và trả về kết quả
            }

            override fun onItemCloseClick(user: User) {

            }
        })

        userRecyclerView.adapter = userAdapter
        userAdapter.notifyDataSetChanged()
    }
}
