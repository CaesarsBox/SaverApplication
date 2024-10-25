package com.example.saverapplication.ui.whatsapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.saverapplication.ui.whatsapp.images.ImagesFragment;
import com.example.saverapplication.ui.whatsapp.videos.VideosFragment;


import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WhatsAppPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public WhatsAppPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ImagesFragment.newInstance();
            case 1:
                return VideosFragment.newInstance();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Images";
            case 1:
                return "Videos";
            default:
                return null;
        }
    }
}




