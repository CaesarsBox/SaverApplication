package com.example.saverapplication.ui.whatsapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WhatsAppFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whats_app, container, false);
        TabLayout bTabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 bViewPager = view.findViewById(R.id.view_pager);

        WhatsAppPagerAdapter pagerAdapter = new WhatsAppPagerAdapter(this);
        bViewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(bTabLayout, bViewPager,
                (tab, position) -> {
                    tab.setText(pagerAdapter.getPageTitle(position)); // Set the title for each tab
                }).attach();

        return view;
    }
}


