package com.example.saverapplication.ui.whatsapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saverapplication.MainActivity;
import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.InnerPagerAdapter;
import com.example.saverapplication.ui.adapters.WhatsAppPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WhatsAppFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setupViewPager();
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    public WhatsAppFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whats_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager= view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new InnerPagerAdapter(this));
        tabLayout = view.findViewById(R.id.tab_layout);

        if (hasStoragePermission()) {
            setupViewPager();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setOuterSwipeEnabled(false); // or true based on your logic
        }
    }


    private void setupViewPager() {
        WhatsAppPagerAdapter adapter = new WhatsAppPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Images");
            } else {
                tab.setText("Videos");
            }
        }).attach();
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}


