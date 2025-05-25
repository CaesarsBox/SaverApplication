package com.example.saverapplication.ui.notis;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.settings.SettingsFragment;

import android.content.Context;
import android.content.SharedPreferences;

public class NewsActivity extends AppCompatActivity implements SettingsFragment.OnSettingsChangedListener {

    private TextView promoStatusMessage;
    private EditText emailEditText;
    private Button subscribeButton;
    private Button settingsButton;
    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        promoStatusMessage = findViewById(R.id.promoStatusMessage);
        emailEditText = findViewById(R.id.emailEditText);
        subscribeButton = findViewById(R.id.subscribeButton);
        settingsButton = findViewById(R.id.settingsButton);

        checkPromoNotificationStatus();

        settingsButton.setOnClickListener(v -> openSettingsFragment());

        // OnBackPressedDispatcher for back navigation handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();  // Closes the activity when back stack is empty
                }
            }
        });
    }

    private void checkPromoNotificationStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        boolean promoNotify = sharedPreferences.getBoolean("promoNotify", false);

        if (promoNotify) {
            promoStatusMessage.setText("Promotional notifications are enabled.");
            settingsButton.setVisibility(View.GONE);
        } else {
            promoStatusMessage.setText("Promotional notifications are disabled. Please enable them.");
            settingsButton.setVisibility(View.VISIBLE);
        }
    }

    private void openSettingsFragment() {
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.news_fragment_container, settingsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (settingsChanged) {
            checkPromoNotificationStatus(); // Refresh promotional notification status
            settingsChanged = false;        // Reset the flag
        }
    }

    // This method is called by SettingsFragment when settings are changed
    @Override
    public void onSettingsChanged() {
        settingsChanged = true;
    }
}
