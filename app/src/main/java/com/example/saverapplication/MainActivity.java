package com.example.saverapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        // Check storage permission based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30 and above)
            return Environment.isExternalStorageManager();
        } else {
            // Below Android 11
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+), request MANAGE_EXTERNAL_STORAGE
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            // For Android 10 and below, request WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            // Check if storage permission is granted for Android 10 and below
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed
                setupViewPager();
            } else {
                // Permission denied, handle accordingly
                Log.e("MainActivity", "Storage permission denied.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            // For Android 11+ (API 30+), check if MANAGE_EXTERNAL_STORAGE was granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted, proceed
                    setupViewPager();
                } else {
                    // Permission denied, handle accordingly
                    Log.e("MainActivity", "Manage external storage permission denied.");
                }
            }
        }
    }

    // Define the FragmentPagerAdapter
    private static class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);  // Use modern behavior
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
                    return new WhatsAppFragment(); // Default to WhatsAppFragment
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

