package com.example.saverapplication.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import com.example.saverapplication.R;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ScrollView rootLayout;
    private List<LinearLayout> settingCards = new ArrayList<>();
    private List<TextView> settingTexts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);

        // Notification Settings
        SwitchCompat newStatusNotifySwitch = view.findViewById(R.id.statusNotifySwitch);
        boolean isStatusNotifyEnabled = sharedPreferences.getBoolean("newStatusNotify", false);
        newStatusNotifySwitch.setChecked(isStatusNotifyEnabled);

        newStatusNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("newStatusNotify", isChecked);
            editor.apply();

            String message = isChecked ? "New Status Notifications Enabled" : "New Status Notifications Disabled";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Promotional Settings
        SwitchCompat promoNotifySwitch = view.findViewById(R.id.switch_promotional_notifications);
        boolean isPromoNotifyEnabled = sharedPreferences.getBoolean("promoNotify", true);
        promoNotifySwitch.setChecked(isPromoNotifyEnabled);
        promoNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("promoNotify", isChecked);
            editor.apply();

            String message = isChecked ? "Promotional Notifications Enabled" : "Promotional Notifications Disabled";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Night Mode Settings
        rootLayout = view.findViewById(R.id.root_layout);
        SwitchCompat nightModeSwitch = view.findViewById(R.id.switch_night_mode);
        initializeSettingCards(view);
        initializeSettingTexts(view);
        boolean isNightModeEnabled = sharedPreferences.getBoolean("nightMode", false);
        nightModeSwitch.setChecked(isNightModeEnabled);

        applyTheme(view, isNightModeEnabled);

        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("nightMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(getContext(), "Night Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Night Mode Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        // Ad Settings
        SwitchCompat adsPersonalizationSwitch = view.findViewById(R.id.switch_ads_personalization);

        boolean isAdsPersonalizationOptedOut = sharedPreferences.getBoolean("adsPersonalization", false);
        adsPersonalizationSwitch.setChecked(isAdsPersonalizationOptedOut);

        adsPersonalizationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("adsPersonalization", isChecked);
            editor.apply();
            if (isChecked) {
                Toast.makeText(getContext(), "Ads Personalization Opted Out", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ads Personalization Enabled", Toast.LENGTH_SHORT).show();
            }
        });

        //Terms and Policies
        TextView termsTextView = view.findViewById(R.id.terms_of_use);
        termsTextView.setOnClickListener(v -> showDialog("Terms of Use", getString(R.string.terms_of_use_text)));

        TextView privacyTextView = view.findViewById(R.id.privacy_policy);
        privacyTextView.setOnClickListener(v -> showDialog("Privacy Policy", getString(R.string.privacy_policy_text)));

        return view;
    }

    private void initializeSettingCards(View view) {
        settingCards = new ArrayList<>();
        settingCards.add(view.findViewById(R.id.card_new_status_available));
        settingCards.add(view.findViewById(R.id.card_promotional_notifications));
        settingCards.add(view.findViewById(R.id.card_night_mode));
        settingCards.add(view.findViewById(R.id.card_opt_out_ads_personalization));
        settingCards.add(view.findViewById(R.id.card_support_the_team));
    }

    private void initializeSettingTexts(View view) {
        settingTexts = new ArrayList<>();
        settingTexts.add(view.findViewById(R.id.text_new_status_available));
        settingTexts.add(view.findViewById(R.id.text_promotional_notifications));
        settingTexts.add(view.findViewById(R.id.text_night_mode));
        settingTexts.add(view.findViewById(R.id.text_opt_out_ads_personalization));
        settingTexts.add(view.findViewById(R.id.text_support_the_team));
        settingTexts.add(view.findViewById(R.id.write_suggestion));
        settingTexts.add(view.findViewById(R.id.clear_app_cache));
    }

    private void applyTheme(View view, boolean isDarkMode) {
        int backgroundColor = isDarkMode ? ContextCompat.getColor(requireContext(), R.color.dark_background) : ContextCompat.getColor(requireContext(), R.color.light_background);
        int cardBackgroundColor = isDarkMode ? ContextCompat.getColor(requireContext(), R.color.dark_card_background) : ContextCompat.getColor(requireContext(), R.color.light_card_background);
        int textColor = isDarkMode ? ContextCompat.getColor(requireContext(), R.color.dark_text_color) : ContextCompat.getColor(requireContext(), R.color.light_text_color);

        rootLayout.setBackgroundColor(backgroundColor);

        for (LinearLayout card : settingCards) {
            card.setBackgroundColor(cardBackgroundColor);
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
        TextView supportTeamText = view.findViewById(R.id.text_support_the_team);
        TextView writeSuggestionText = view.findViewById(R.id.write_suggestion);
        TextView clearAppCacheText = view.findViewById(R.id.clear_app_cache);

        int aboutItemBackgroundColor = isDarkMode ? ContextCompat.getColor(requireContext(), R.color.dark_card_background) : ContextCompat.getColor(requireContext(), R.color.light_card_background);

        supportTeamText.setBackgroundColor(aboutItemBackgroundColor);
        writeSuggestionText.setBackgroundColor(aboutItemBackgroundColor);
        clearAppCacheText.setBackgroundColor(aboutItemBackgroundColor);

        supportTeamText.setTextColor(textColor);
        writeSuggestionText.setTextColor(textColor);
        clearAppCacheText.setTextColor(textColor);
    }
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();

    }
}
