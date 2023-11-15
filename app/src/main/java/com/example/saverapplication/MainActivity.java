package com.example.saverapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.saverapplication.ui.downloads.DownloadsFragment;
import com.example.saverapplication.ui.whatsapp.WhatsAppFragment;
import com.example.saverapplication.ui.whatsapp.images.ImageData;
import com.example.saverapplication.ui.whatsapp.videos.VideoData;
import com.example.saverapplication.ui.whatsapp4b.WBFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static final long SPLASH_SCREEN_DELAY = 2000; // 2 seconds


    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);

        // Check storage permission
        if (hasStoragePermission()) {
            // If permission is already granted, proceed with setting up the ViewPager
            setupViewPager();
        } else {
            // Request storage permission
            requestStoragePermission();
        }
 }

    private void setupViewPager() {
        // Create a FragmentPagerAdapter to manage the fragments
        FragmentPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private boolean hasStoragePermission() {
        // Check if the storage permission is granted
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        // Request the storage permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            // Check if the storage permission is granted after the user responds to the request
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with setting up the ViewPager
                setupViewPager();
            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
            }
        }
    }

    // Define the FragmentPagerAdapter
    private static class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WhatsAppFragment();
                case 1:
                    return new WBFragment();
                case 2:
                    return new DownloadsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3; // Three tabs: WhatsApp, WB, Downloads
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WhatsApp";
                case 1:
                    return "WB";
                case 2:
                    return "Downloads";
                default:
                    return "";
            }
        }
    }
}
