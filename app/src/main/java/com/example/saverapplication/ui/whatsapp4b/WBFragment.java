package com.example.saverapplication.ui.whatsapp4b;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.whatsapp.WhatsAppPagerAdapter;
import com.example.saverapplication.ui.whatsapp.images.ImageData;
import com.example.saverapplication.ui.whatsapp.videos.VideoData;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class WBFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w_b, container, false);
        tabLayout = view.findViewById(R.id.wTab_layout);
        viewPager = view.findViewById(R.id.wView_pager);

        // Set up the ViewPager and TabLayout
        WhatsAppBPagerAdapter pagerAdapter = new WhatsAppBPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
   
}

