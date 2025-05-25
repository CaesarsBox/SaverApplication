
package com.example.saverapplication.ui.downloads;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.FullscreenVideoAdapter;

import java.io.File;
import java.util.List;

public class SavedVideoViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<File> videoFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_video_viewer);

        videoFiles = (List<File>) getIntent().getSerializableExtra("videos");
        int startPosition = getIntent().getIntExtra("position", 0);

        viewPager = findViewById(R.id.videoViewPagerSaved);
        ImageButton shareButton = findViewById(R.id.videoShareButtonSaved);

        viewPager.setAdapter(new FullscreenVideoAdapter(videoFiles, this));
        viewPager.setCurrentItem(startPosition, false);

        findViewById(R.id.backButtonSaved).setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v -> {
            File currentVideo = videoFiles.get(viewPager.getCurrentItem());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", currentVideo));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        });
    }
}
