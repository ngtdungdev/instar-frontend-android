package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.UserService

class SharePostBottomSheetDialogAdapter(
    private val context: Context,
    private val dataList: List<User>,
    private val lifecycleScope: LifecycleCoroutineScope
) : RecyclerView.Adapter<SharePostBottomSheetDialogAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvt: ImageView = itemView.findViewById(R.id.imgAvt)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val imgCheck: ImageView = itemView.findViewById(R.id.imgCheck)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_share_post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Làm bất kỳ xử lý nào bạn cần cho mỗi mục trong danh sách ở đây
        val dataItem = dataList[position]

        holder.txtName.text = dataItem.fullname

        // Gắn dữ liệu và xử lý sự kiện cho các thành phần khác trong ViewHolder
        // Ví dụ:
        holder.imageButton.setOnClickListener {
            // Xử lý sự kiện khi ImageButton được nhấp vào
        }

        Glide.with(context)
            .load(dataItem.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(holder.imgAvt)

        // Hiển thị hoặc ẩn imgCheck tùy thuộc vào điều kiện của bạn
        // Ví dụ:
        if (position == 0) {
            holder.imgCheck.visibility = View.VISIBLE
        } else {
            holder.imgCheck.visibility = View.GONE
        }

        // Gắn CardView vào lớp CardView của ViewHolder để tùy chỉnh
        // Ví dụ:
        holder.cardView.setOnClickListener {
            // Xử lý sự kiện khi CardView được nhấp vào
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
