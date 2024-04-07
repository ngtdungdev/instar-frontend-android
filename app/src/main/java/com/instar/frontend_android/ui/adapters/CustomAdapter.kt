import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Post

class CustomAdapter(
    private val context: Context,
    private var dataList: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    fun setData(dataList: List<Post>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_on_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Post = dataList[position]
        val firstMedia = data.fileUploads?.firstOrNull()

        firstMedia?.let {
            Glide.with(context)
                .load(it.url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.imageView)
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener {
            onItemClick(data)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
