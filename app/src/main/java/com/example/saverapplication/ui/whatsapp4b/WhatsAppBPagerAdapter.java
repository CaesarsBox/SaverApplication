package com.example.saverapplication.ui.whatsapp4b;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WhatsAppBPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    public WhatsAppBPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ImageBFragment.newInstance();
            case 1:
                return VideosBFragment.newInstance();
            default:
                return ImageBFragment.newInstance(); // Fallback
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
