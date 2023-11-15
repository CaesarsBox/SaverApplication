package com.example.saverapplication.ui.whatsapp4b;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WhatsAppBPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2; // Number of tabs (Images and Videos)

    public WhatsAppBPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment based on the tab position
        switch (position) {
            case 0:
                return ImageBFragment.newInstance(); // Create an ImagesFragment
            case 1:
                return VideosBFragment.newInstance(); // Create a VideosFragment
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
