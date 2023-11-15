package com.example.saverapplication.ui.whatsapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.saverapplication.ui.whatsapp.images.ImagesFragment;
import com.example.saverapplication.ui.whatsapp.videos.VideosFragment;


public class WhatsAppPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2; // Number of tabs (Images and Videos)

    public WhatsAppPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment based on the tab position
        switch (position) {
            case 0:
                return ImagesFragment.newInstance(); // Create an ImagesFragment
            case 1:
                return VideosFragment.newInstance(); // Create a VideosFragment
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES; // Return the number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set tab titles (e.g., "Images" and "Videos")
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
