package com.instar.frontend_android.ui.fragments

import CustomAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyPostFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewPost) // Sử dụng id recyclerViewPost
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val dataList = listOf(
            "https://images.hdqwalls.com/wallpapers/sage-valorant-4k-kq.jpg",
            "https://images.hdqwalls.com/download/2020-valorant-game-4k-e5-1920x1080.jpg",
            "https://images.hdqwalls.com/download/jett-valorant-4k-k4-2560x1440.jpg",
            "https://images.hdqwalls.com/wallpapers/sage-valorant-4k-kq.jpg",
            "https://images.hdqwalls.com/download/2020-valorant-game-4k-e5-1920x1080.jpg",
            "https://images.hdqwalls.com/download/jett-valorant-4k-k4-2560x1440.jpg",
            "https://images.hdqwalls.com/wallpapers/sage-valorant-4k-kq.jpg",
            "https://images.hdqwalls.com/download/2020-valorant-game-4k-e5-1920x1080.jpg",
            "https://images.hdqwalls.com/download/jett-valorant-4k-k4-2560x1440.jpg",
            "https://images.hdqwalls.com/wallpapers/sage-valorant-4k-kq.jpg",
            "https://images.hdqwalls.com/download/2020-valorant-game-4k-e5-1920x1080.jpg",
            "https://images.hdqwalls.com/download/jett-valorant-4k-k4-2560x1440.jpg",
            "https://images.hdqwalls.com/wallpapers/sage-valorant-4k-kq.jpg",
            "https://images.hdqwalls.com/download/2020-valorant-game-4k-e5-1920x1080.jpg",
            "https://images.hdqwalls.com/download/jett-valorant-4k-k4-2560x1440.jpg",
            // Thêm các URL hình ảnh khác vào đây
        )

        val adapter = CustomAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
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
