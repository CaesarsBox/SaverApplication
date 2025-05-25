package com.example.saverapplication.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.saverapplication.ui.whatsapp.WhatsAppImageFragment;
import com.example.saverapplication.ui.whatsapp.WhatsAppVideoFragment;


public class WhatsAppPagerAdapter extends FragmentStateAdapter {

    public WhatsAppPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new WhatsAppImageFragment();
        } else {
            return new WhatsAppVideoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}




