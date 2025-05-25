package com.example.saverapplication;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.ui.downloads.SavedFragment;
import com.example.saverapplication.ui.notis.NewsActivity;
import com.example.saverapplication.ui.notis.NotificationsActivity;
import com.example.saverapplication.ui.settings.SettingsFragment;
import com.example.saverapplication.ui.settings.StatusMonitorService;
import com.example.saverapplication.ui.whatsapp.WhatsAppFragment;
import com.example.saverapplication.ui.whatsapp4b.WBFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity implements OnSettingsChangedListener {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_OPEN_WHATSAPP_STATUSES = 1001;

    private static final String TAG = "MainActivity";
    ViewPager2 outerViewPager;
    public boolean isSettingsChanged = false;

    private ActivityResultLauncher<Intent> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("from_notification", false)) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(1001); // Same ID used in sendStatusNotification
        }
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotifyEnabled = prefs.getBoolean("status_notify", true);

        if (isNotifyEnabled) {
            Intent serviceIntent = new Intent(this, StatusMonitorService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }


        // Initialize Mobile Ads
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Load banner ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("whatsapp_statuses_uri", null) == null) {
            openStatusesFolderPicker();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupSettingsChangeListener();
        setupIconListeners();

        if (hasStoragePermission()) {
            setupViewPager();
        } else {
            requestStoragePermission();
        }
        outerViewPager = findViewById(R.id.container);

    }
    public void setOuterSwipeEnabled(boolean enabled) {
        if (outerViewPager != null) {
            outerViewPager.setUserInputEnabled(enabled);
        }
    }

    private void setupSettingsChangeListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences1, key) -> {
            assert key != null;
            if (key.equals("night_mode")) {
                boolean isNightModeEnabled = sharedPreferences1.getBoolean(key, false);
                setNightMode(isNightModeEnabled);
            }
        });
    }

    private void setNightMode(boolean isNightModeEnabled) {
        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);  // Enable dark mode
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);   // Enable light mode
        }
    }

    private void setupIconListeners() {
        findViewById(R.id.notifications_icon).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class)));

        findViewById(R.id.news_icon).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, NewsActivity.class)));
    }

    private void openStatusesFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_OPEN_WHATSAPP_STATUSES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_WHATSAPP_STATUSES && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            assert treeUri != null;
            getContentResolver().takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString("whatsapp_statuses_uri", treeUri.toString())
                    .apply();

            Toast.makeText(this, "Access granted. Reopen the app to view statuses.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            storagePermissionLauncher.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabs);
        MyPagerAdapter adapter = new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();
    }

    @Override
    public void onSettingsChanged(String key) {
        Log.d("MainActivity", "Setting changed: " + key);
        isSettingsChanged = true;

        if ("nightMode".equals(key)) {
            recreate();
        }
    }

    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(MainActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1: return new WBFragment();
                case 2: return new SavedFragment();
                case 3: return new SettingsFragment();
                default: return new WhatsAppFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "WhatsApp";
                case 1: return "WB";
                case 2: return "Saved";
                case 3: return "Settings";
                default: return "";
            }
        }
    }
}



