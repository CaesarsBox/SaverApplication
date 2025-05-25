package com.example.saverapplication.ui.whatsapp4b;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WBBusinessPagerAdapter extends FragmentStateAdapter {

    public WBBusinessPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new WBImageFragment() : new WBVideoFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
