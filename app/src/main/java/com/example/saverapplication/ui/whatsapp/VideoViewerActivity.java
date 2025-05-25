package com.example.saverapplication.ui.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.FullscreenVideoAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VideoViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<File> videoFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);

        videoFiles = (List<File>) getIntent().getSerializableExtra("videos");
        int startPosition = getIntent().getIntExtra("position", 0);

        viewPager = findViewById(R.id.videoViewPager);
        ImageButton shareButton = findViewById(R.id.videoShareButton);
        ImageButton saveButton = findViewById(R.id.saveButton);

        viewPager.setAdapter(new FullscreenVideoAdapter(videoFiles, this));
        viewPager.setCurrentItem(startPosition, false);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v -> {
            File currentVideo = videoFiles.get(viewPager.getCurrentItem());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", currentVideo));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        });

        saveButton.setOnClickListener(v -> {
            File src = videoFiles.get(viewPager.getCurrentItem());
            File dest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/Status Saver", src.getName());
            try {
                FileUtils.copyFile(src, dest);
                Toast.makeText(this, "Saved to Status Saver", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
