package com.example.saverapplication.ui.whatsapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.whatsapp.images.ImageData;
import com.example.saverapplication.ui.whatsapp.videos.VideoData;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class WhatsAppFragment extends Fragment {
    private TabLayout bTabLayout;
    private ViewPager bViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whats_app, container, false);
        bTabLayout = view.findViewById(R.id.tab_layout);
        bViewPager = view.findViewById(R.id.view_pager);

        // Set up the ViewPager and TabLayout
        WhatsAppPagerAdapter pagerAdapter = new WhatsAppPagerAdapter(getChildFragmentManager());
        bViewPager.setAdapter(pagerAdapter);
        bTabLayout.setupWithViewPager(bViewPager);

        return view;
    }
    public void refreshData(List<ImageData> updatedImageList, List<VideoData> updatedVideoList) {
        // Update your UI with the new data
    }
}
