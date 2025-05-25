package com.example.saverapplication.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.saverapplication.ui.whatsapp.WhatsAppImageFragment;
import com.example.saverapplication.ui.whatsapp.WhatsAppVideoFragment;

public class InnerPagerAdapter extends FragmentStateAdapter {

    public InnerPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new WhatsAppImageFragment() : new WhatsAppVideoFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

