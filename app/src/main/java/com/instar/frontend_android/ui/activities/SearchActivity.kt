package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivitySearchAccountBinding
import com.instar.frontend_android.interfaces.InterfaceUtils
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.SearchAdapter
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchAccountBinding
    private lateinit var layout: View
    private lateinit var btnHome: ImageView
    private lateinit var btnSearch: ImageView
    private lateinit var btnPostUp: ImageButton
    private lateinit var btnReel: ImageView
    private lateinit var btnPersonal: View
    private lateinit var btnRemove: ImageButton
    private lateinit var edtSearch: EditText
    private lateinit var query: EditText
    private lateinit var userService: UserService
    private lateinit var userList: MutableList<User>
    private lateinit var tagList: MutableList<User>
    private lateinit var userAdapter: SearchAdapter
    private lateinit var userRecyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        layout = binding.gridLayout
        userRecyclerView =binding.recyclerViewAccount
        btnPostUp = binding.btnPostUp
        btnPersonal = binding.btnPersonal
        btnHome = binding.btnHome
        btnReel = binding.btnReel
        btnSearch = binding.btnSearch
        edtSearch = binding.edtSearch
        query = binding.edtSearch
        btnRemove = binding.iconDelete

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)

        initView()
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        btnPersonal.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        widthLayout = (getScreenWidth(this) - dpToPx(30 * 4 + 10 * 2 + 37)) / 4
        setMargin(btnSearch)
        setMargin(btnPersonal)
        setMargin(btnReel)
        setMargin(btnPostUp)

        val viewEditText = ViewEditText()
        viewEditText.EditTextTag(edtSearch,  btnRemove)
        viewEditText.setOnItemFocusClick(object : ViewEditText.OnItemClick {
            override fun onEyesChange(view: View) {
            }
            override fun onRemoveChange(view: View) {
                if (edtSearch.text.toString().isEmpty()) setMessage()
            }
        })
        val debounceHandler = Handler(Looper.getMainLooper())
        var debounceRunnable: Runnable? = null
        query.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let {
                    debounceHandler.removeCallbacks(it)
                }
                debounceRunnable = Runnable {
                    if (s.toString().isEmpty()) {
                        setQuery()
                    } else {
                        search();
                    }
                }
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
                if (response != null && response.data?.followingUsers != null) {
                    userList = response.data.followingUsers as MutableList<User>
                    Toast.makeText(this@SearchActivity, userList.toString(), Toast.LENGTH_LONG).show()
                    updateUserList() // Cập nhật dữ liệu sau khi nhận kết quả
                } else {
                    Log.e("Error", "Failed to get following users")
                }
            }
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserList() {
        userAdapter = SearchAdapter(this, userList, object : InterfaceUtils.OnItemClickListener {
            override fun onItemClick(user: User) {
                hideKeyboard()
                val intent = Intent(this@SearchActivity, ProfileOtherActivity::class.java)
                intent.putExtra("userOther", user)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            override fun onItemCloseClick(user: User) {
            }
        })
        userRecyclerView.adapter = userAdapter
        userAdapter.notifyDataSetChanged()
    }
    fun setQuery() {
        query.hint = "Tìm kiếm"
    }
    private fun setMessage() {
        edtSearch.hint = "Tìm kiếm"
    }

    private var widthLayout: Int? = null

    @RequiresApi(Build.VERSION_CODES.R)
    fun setMargin(view: View) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.leftMargin = widthLayout!!
        view.layoutParams = layoutParams
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
    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
