package com.example.saverapplication.ui.whatsapp;


import static org.apache.commons.io.FileUtils.copyFile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.FullscreenImageAdapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<File> imageFiles;
    private int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        viewPager = findViewById(R.id.imageViewPager);
        imageFiles = (List<File>) getIntent().getSerializableExtra("images");
        startPosition = getIntent().getIntExtra("position", 0);

        viewPager.setAdapter(new FullscreenImageAdapter(imageFiles, this));
        viewPager.setCurrentItem(startPosition, false);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.shareButton).setOnClickListener(v -> {
            File currentImage = imageFiles.get(viewPager.getCurrentItem());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", currentImage);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        findViewById(R.id.downloadButton).setOnClickListener(v -> {
            File src = imageFiles.get(viewPager.getCurrentItem());
            File dest = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "Status Saver/" + src.getName());

            try {
                copyFile(src, dest);

                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(dest));
                sendBroadcast(scanIntent);

                Toast.makeText(this, "Saved to Status Saver", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }
}





