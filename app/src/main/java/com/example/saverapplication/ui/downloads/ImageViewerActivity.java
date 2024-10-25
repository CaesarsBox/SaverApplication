package com.example.saverapplication.ui.downloads;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ViewPager2 imageViewPager = findViewById(R.id.imageViewPager);
        String imageUri = getIntent().getStringExtra("imageUri");
        List<String> imagePaths = new ArrayList<>();

        if (imageUri != null) {
            imagePaths.add(imageUri);
        } else if (getIntent().hasExtra("imagePaths")) {
            imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        }

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imagePaths);
        imageViewPager.setAdapter(imagePagerAdapter);

        int startPosition = getIntent().getIntExtra("startPosition", 0);
        if (startPosition >= 0 && !imagePaths.isEmpty()) {
            imageViewPager.setCurrentItem(startPosition, false);
        }
    }
}



