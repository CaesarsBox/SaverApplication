package com.example.saverapplication.ui.whatsapp4b;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WhatsAppBPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    public WhatsAppBPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ImageBFragment.newInstance();
            case 1:
                return VideosBFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
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
