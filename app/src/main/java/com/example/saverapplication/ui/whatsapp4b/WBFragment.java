package com.example.saverapplication.ui.whatsapp4b;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WBFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w_b, container, false);
        TabLayout tabLayout = view.findViewById(R.id.wTab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.wView_pager);

        WhatsAppBPagerAdapter pagerAdapter = new WhatsAppBPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Images");
                            break;
                        case 1:
                            tab.setText("Videos");
                            break;
                    }
                }).attach();

        return view;
    }
}
