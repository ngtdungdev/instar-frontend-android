package com.instar.frontend_android.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.DTO.Images;

import java.util.List;

public class NewsFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Images> images;
    private Context context;
    private OnItemClickItem listener;
    public NewsFollowAdapter(Context context, List<Images> images) {
        this.context = context;
        this.images = images;
    }
    public void setOnItemClickItem(OnItemClickItem listener) {
        this.listener = listener;
    }
    public interface OnItemClickItem {
        void onPersonalClick(int position);
        void onFriendClick(int position);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Images.TYPE_PERSONAL_AVATAR) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_personal_avatar, parent, false);
            return new PersonalAvatar(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_friend_avatar, parent, false);
            return new FriendAvatar(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Images item = images.get(position);
        if (holder instanceof PersonalAvatar) {
            bindPersonalAvatar(((PersonalAvatar) holder), item);
        } else if (holder instanceof FriendAvatar) {
            bindFriendAvatar(((FriendAvatar) holder), item);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onFriendClick(holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    public void bindPersonalAvatar(PersonalAvatar data, Images item) {
        if(item.getUrl() != null) {
            Glide.with(context)
                    .load(item.getUrl())
                    .into(data.imageButton);
        } else data.imageButton.setBackgroundResource(R.drawable.baseline_account_circle_24);
        data.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("conmeo", "onClick: ");
            }
        });
    }

    public void bindFriendAvatar(FriendAvatar data, Images item) {
        if(item.getUrl() != null) {
            Glide.with(context)
                    .load(item.getUrl())
                    .into(data.imageButton);
        } else data.imageButton.setBackgroundResource(R.drawable.baseline_account_circle_24);
        data.nameAvatar.setText(item.getName());
    }

    public class PersonalAvatar extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        View layout;
        public PersonalAvatar(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imageButton = itemView.findViewById(R.id.imageButton);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return images.get(position).getType();
    }

    public class FriendAvatar extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        ImageView imageBorder;
        TextView nameAvatar;
        public FriendAvatar(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
            imageBorder = itemView.findViewById(R.id.imageBorder);
            nameAvatar = itemView.findViewById(R.id.nameAvatar);
        }
    }
    @Override
    public int getItemCount() {
        return images.size();
    }
}
