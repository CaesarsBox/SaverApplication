package com.example.saverapplication.ui.downloads;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saverapplication.R;

import java.io.File;

import androidx.core.content.FileProvider;

import androidx.core.content.FileProvider;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VideoView videoView = findViewById(R.id.videoView);

        String videoPath = getIntent().getStringExtra("videoPath");

        if (videoPath != null && !videoPath.isEmpty()) {
            File videoFile = new File(videoPath);

            if (videoFile.exists()) {
                Uri videoUri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".provider",
                        videoFile
                );

                videoView.setVideoURI(videoUri);

                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                videoView.setOnPreparedListener(mp -> videoView.start());
            } else {
                Toast.makeText(this, "Video file not found", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid video path", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
