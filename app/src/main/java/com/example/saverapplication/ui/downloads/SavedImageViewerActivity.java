package com.example.saverapplication.ui.downloads;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.FullscreenImageAdapter;

import java.io.File;
import java.util.List;

public class SavedImageViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<File> imageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image_viewer);

        imageFiles = (List<File>) getIntent().getSerializableExtra("images");
        int startPosition = getIntent().getIntExtra("position", 0);

        viewPager = findViewById(R.id.imageViewPagerSaved);
        ImageButton shareButton = findViewById(R.id.imageShareButtonSaved);

        viewPager.setAdapter(new FullscreenImageAdapter(imageFiles, this));
        viewPager.setCurrentItem(startPosition, false);

        findViewById(R.id.backButtonSaved).setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v -> {
            File currentImage = imageFiles.get(viewPager.getCurrentItem());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", currentImage));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        });
    }
}
