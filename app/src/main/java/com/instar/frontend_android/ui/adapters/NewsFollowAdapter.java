package com.instar.frontend_android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.DTO.Images;

import java.util.List;

public class NewsFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Images> images;
    public NewsFollowAdapter(List<Images> images) {
        this.images = images;
    }


    @NonNull
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
        }
    }

    public void bindPersonalAvatar(PersonalAvatar data, Images item) {
        data.imageButton.setBackgroundResource(item.getImgPath());
    }

    public void bindFriendAvatar(FriendAvatar data, Images item) {
        data.imageButton.setBackgroundResource(item.getImgPath());
        data.nameAvatar.setText(item.getName());
    }

    public class PersonalAvatar extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        public PersonalAvatar(@NonNull View itemView) {
            super(itemView);
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
