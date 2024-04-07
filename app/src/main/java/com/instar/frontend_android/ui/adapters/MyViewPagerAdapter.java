package com.instar.frontend_android.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.instar.frontend_android.ui.fragments.MyPostFragment;
import com.instar.frontend_android.ui.fragments.MyPostSavedFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    private String userId;
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userId) {
        super(fragmentActivity);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MyPostFragment(userId);
            case 1:
                return new MyPostSavedFragment(userId);
            default:
                return new MyPostFragment(userId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
