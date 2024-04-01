import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Post

class CustomAdapter(private val context: Context, private val dataList: List<Post>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_on_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        Glide.with(context)
            .load(data)
        val data: Post = dataList[position]
        val firstMedia = data.fileUploads[0]

        Glide.with(context)
            .load(firstMedia.url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
