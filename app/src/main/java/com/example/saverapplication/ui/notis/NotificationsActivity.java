package com.example.saverapplication.ui.notis;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saverapplication.R;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat.Type;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_notifications);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
