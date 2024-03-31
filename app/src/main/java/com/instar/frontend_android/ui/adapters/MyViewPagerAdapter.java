package com.instar.frontend_android.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.instar.frontend_android.ui.fragments.MyPostFragment;
import com.instar.frontend_android.ui.fragments.MyPostSavedFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MyPostFragment();
            case 1:
                return new MyPostSavedFragment();
            default:
                return new MyPostFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
