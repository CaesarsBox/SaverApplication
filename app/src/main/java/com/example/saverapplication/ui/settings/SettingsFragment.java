package com.example.saverapplication.ui.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.notis.NewsActivity;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ScrollView rootLayout;
    private final List<LinearLayout> settingCards = new ArrayList<>();
    private final List<TextView> settingTexts = new ArrayList<>();
    private OnSettingsChangedListener settingsChangedListener;

    public interface OnSettingsChangedListener {
        void onSettingsChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof OnSettingsChangedListener) {
            settingsChangedListener = (OnSettingsChangedListener) parent;
        } else if (context instanceof OnSettingsChangedListener) {
            settingsChangedListener = (OnSettingsChangedListener) context;
        } else {
            Log.w("SettingsFragment", "No OnSettingsChangedListener found.");
        }
    }


    private void notifySettingsChanged() {
        if (settingsChangedListener != null) {
            settingsChangedListener.onSettingsChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        rootLayout = view.findViewById(R.id.root_layout);
        initializeSettingCards(view);
        initializeSettingTexts(view);

        setupStatusNotifySwitch(view);
        setupPromoNotifySwitch(view);
        setupNightModeSwitch(view);
        setupAdsPersonalizationSwitch(view);
        setupTermsAndPolicies(view);

        SwitchCompat statusNotifySwitch = view.findViewById(R.id.statusNotifySwitch);
        boolean isNotifyEnabled = sharedPreferences.getBoolean("status_notify", true);
        statusNotifySwitch.setChecked(isNotifyEnabled);

        if (isNotifyEnabled) {
            Intent serviceIntent = new Intent(requireContext(), StatusMonitorService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        }

        TextView supportTextView = view.findViewById(R.id.text_support_the_team);
        supportTextView.setOnClickListener(v -> showSupportDialog());
        TextView clearCache = view.findViewById(R.id.clear_app_cache);
        clearCache.setOnClickListener(v -> showClearCacheDialog());
        TextView writeSuggestion = view.findViewById(R.id.write_suggestion);
        writeSuggestion.setOnClickListener(v -> {
            showSuggestionDialog();
        });



        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        applyTheme(view, isDarkMode);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setupStatusNotifySwitch(View view) {
        SwitchCompat switchView = view.findViewById(R.id.statusNotifySwitch);
        boolean isEnabled = sharedPreferences.getBoolean("newStatusNotify", false);
        switchView.setChecked(isEnabled);

        switchView.setOnCheckedChangeListener((btn, isChecked) -> {
            sharedPreferences.edit().putBoolean("newStatusNotify", isChecked).apply();

            if (isChecked) {
                enableStatusNotifications();  // Enable immediate notification
                startStatusMonitoringService();  // Start background service
                Toast.makeText(getContext(), "New Status Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                disableStatusNotifications();  // Disable notifications
                stopStatusMonitoringService();  // Stop background service
                Toast.makeText(getContext(), "New Status Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Start the background service
    private void startStatusMonitoringService() {
        Intent serviceIntent = new Intent(getContext(), StatusMonitorService.class);
        getContext().startService(serviceIntent);  // Start the service
    }

    // Stop the background service
    private void stopStatusMonitoringService() {
        Intent serviceIntent = new Intent(getContext(), StatusMonitorService.class);
        getContext().stopService(serviceIntent);  // Stop the service
    }

    private void enableStatusNotifications() {
        // Create NotificationHelper and the notification channel
        NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.createNotificationChannel();

        // Send a test notification (you can modify this to show actual status updates)
        notificationHelper.sendStatusNotification("New Status Available", "You have a new status update available.");

        Log.d("SettingsFragment", "Status notifications enabled");

        // If needed, you can set up a background service to keep track of new statuses and send notifications.
    }

    private void disableStatusNotifications() {
        // Create NotificationHelper and cancel notifications
        NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.cancelNotifications();

        Log.d("SettingsFragment", "Status notifications disabled");

        // If you are tracking statuses in the background, you can stop notifications here.
    }



    private void setupPromoNotifySwitch(View view) {
        SwitchCompat switchView = view.findViewById(R.id.switch_promotional_notifications);
        boolean isEnabled = sharedPreferences.getBoolean("promoNotify", true);
        switchView.setChecked(isEnabled);

        switchView.setOnCheckedChangeListener((btn, isChecked) -> {
            sharedPreferences.edit().putBoolean("promoNotify", isChecked).apply();

            // Enable or disable promotional notifications based on the switch
            if (isChecked) {
                enablePromoNotifications();
                Toast.makeText(getContext(), "Promotional Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                disablePromoNotifications();
                Toast.makeText(getContext(), "Promotional Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enablePromoNotifications() {
        // Implement the logic to enable promotional notifications here.
        // For example, schedule a periodic notification or push notifications for promotions.
    }

    private void disablePromoNotifications() {
        // Implement the logic to disable promotional notifications here.
        // Example: Cancel or stop scheduled promotional notifications.
    }

    private void setupNightModeSwitch(View view) {
        SwitchCompat nightModeSwitch = view.findViewById(R.id.switch_night_mode);

        boolean isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        nightModeSwitch.setChecked(isNightMode);

        nightModeSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            if ((isChecked && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) ||
                    (!isChecked && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)) {

                sharedPreferences.edit().putBoolean("nightMode", isChecked).apply();

                AppCompatDelegate.setDefaultNightMode(isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO);

                Toast.makeText(getContext(), isChecked ? "Night Mode Enabled" : "Night Mode Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdsPersonalizationSwitch(View view) {
        SwitchCompat switchView = view.findViewById(R.id.switch_ads_personalization);
        boolean isOptedOut = sharedPreferences.getBoolean("adsPersonalization", false);
        switchView.setChecked(isOptedOut);

        switchView.setOnCheckedChangeListener((btn, isChecked) -> {
            sharedPreferences.edit().putBoolean("adsPersonalization", isChecked).apply();

            // Enable or disable ads personalization based on the switch
            if (isChecked) {
                enableAdsPersonalization();
                Toast.makeText(getContext(), "Ads Personalization Enabled", Toast.LENGTH_SHORT).show();
            } else {
                disableAdsPersonalization();
                Toast.makeText(getContext(), "Ads Personalization Opted Out", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableAdsPersonalization() {
        // Implement the logic to enable ads personalization here.
        // Example: Use Google Mobile Ads SDK to enable personalized ads.
    }

    private void disableAdsPersonalization() {
        // Implement the logic to disable ads personalization here.
        // Example: Use Google Mobile Ads SDK to disable personalized ads.
    }

    private void setupTermsAndPolicies(View view) {
        view.findViewById(R.id.terms_of_use).setOnClickListener(v ->
                showDialog("Terms of Use", getString(R.string.terms_of_use_text)));

        view.findViewById(R.id.privacy_policy).setOnClickListener(v ->
                showDialog("Privacy Policy", getString(R.string.privacy_policy_text)));
    }

    private void showSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Support the Developer ❤️");
        builder.setMessage("Hi, I'm a solo developer behind this app.\n\nIf you'd like to support this project, you can use the options below:\n\n• M-Pesa Number: 0724996136\n• PayPal");

        builder.setPositiveButton("Copy M-Pesa Number", (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("M-Pesa Phone Number", "0724996136");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Phone number copied!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Go to PayPal", (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/ncp/payment/43747E4KHW92L\n"));
            startActivity(browserIntent);
        });

        builder.setNeutralButton("Close", null);
        builder.show();
    }
    private void showClearCacheDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear App Cache")
                .setMessage("Are you sure you want to clear the app cache?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    clearAppCache(requireContext());
                    Toast.makeText(requireContext(), "App cache cleared.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void clearAppCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private void showSuggestionDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_suggestion, null);

        // Find the EditTexts for user name and suggestion
        EditText nameInput = dialogView.findViewById(R.id.edit_name);
        EditText suggestionInput = dialogView.findViewById(R.id.edit_suggestion);

        new AlertDialog.Builder(requireContext())
                .setTitle("Send Suggestion")
                .setView(dialogView)
                .setPositiveButton("Send", (dialog, which) -> {
                    // Get the user's name and suggestion text
                    String name = nameInput.getText().toString().trim();
                    String suggestion = suggestionInput.getText().toString().trim();

                    // Call the method to send the suggestion email
                    sendSuggestionEmail(name, suggestion);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendSuggestionEmail(String name, String suggestion) {
        // If the user did not enter a name, use "Anonymous"
        String subject = "App Suggestion from " + (name.isEmpty() ? "Anonymous" : name);
        // If the suggestion is empty, provide a default message
        String body = suggestion.isEmpty() ? "No suggestion entered." : suggestion;

        // Log the subject and body to check
        Log.d("SendEmail", "Subject: " + subject);
        Log.d("SendEmail", "Body: " + body);

        // Create an email intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:m2kmediake@gmail.com")); // Set recipient email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // Set email subject
        emailIntent.putExtra(Intent.EXTRA_TEXT, body); // Set email body

        // Log the intent details
        Log.d("SendEmail", "Intent created: " + emailIntent);

        // Check if there's an email app to handle the intent
        if (emailIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            Log.d("SendEmail", "Email client found, starting activity.");
            startActivity(emailIntent); // Open email client
        } else {
            Log.d("SendEmail", "No email client found.");
            // Show a toast if no email app is found
            Toast.makeText(requireContext(), "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeSettingCards(View view) {
        settingCards.clear();
        settingCards.add(view.findViewById(R.id.card_new_status_available));
        settingCards.add(view.findViewById(R.id.card_promotional_notifications));
        settingCards.add(view.findViewById(R.id.card_night_mode));
        settingCards.add(view.findViewById(R.id.card_opt_out_ads_personalization));
        settingCards.add(view.findViewById(R.id.card_support_the_team));
    }

    private void initializeSettingTexts(View view) {
        settingTexts.clear();
        settingTexts.add(view.findViewById(R.id.text_new_status_available));
        settingTexts.add(view.findViewById(R.id.text_promotional_notifications));
        settingTexts.add(view.findViewById(R.id.text_night_mode));
        settingTexts.add(view.findViewById(R.id.text_opt_out_ads_personalization));
        settingTexts.add(view.findViewById(R.id.text_support_the_team));
        settingTexts.add(view.findViewById(R.id.write_suggestion));
        settingTexts.add(view.findViewById(R.id.clear_app_cache));
    }

    private void applyTheme(View view, boolean isDarkMode) {
        int backgroundColor = ContextCompat.getColor(requireContext(), isDarkMode ? R.color.dark_background : R.color.light_background);
        int cardColor = ContextCompat.getColor(requireContext(), isDarkMode ? R.color.dark_card_background : R.color.light_card_background);
        int textColor = ContextCompat.getColor(requireContext(), isDarkMode ? R.color.dark_text_color : R.color.light_text_color);

        rootLayout.setBackgroundColor(backgroundColor);

        for (LinearLayout card : settingCards) {
            card.setBackgroundColor(cardColor);
            for (int i = 0; i < card.getChildCount(); i++) {
                View child = card.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(textColor);
                }
            }
        }

        for (TextView textView : settingTexts) {
            textView.setTextColor(textColor);
        }

        updateAboutItems(view, isDarkMode, textColor);
    }

    private void updateAboutItems(View view, boolean isDarkMode, int textColor) {
        int aboutBg = ContextCompat.getColor(requireContext(), isDarkMode ? R.color.dark_card_background : R.color.light_card_background);

        int[] ids = {
                R.id.text_support_the_team,
                R.id.write_suggestion,
                R.id.clear_app_cache
        };

        for (int id : ids) {
            TextView textView = view.findViewById(id);
            textView.setBackgroundColor(aboutBg);
            textView.setTextColor(textColor);
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof NewsActivity) {
            ((NewsActivity) getActivity()).onSettingsChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        settingsChangedListener = null;
    }

    private void changeSetting() {
        notifySettingsChanged();
    }
}
