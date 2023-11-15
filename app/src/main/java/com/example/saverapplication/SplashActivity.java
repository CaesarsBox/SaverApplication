package com.example.saverapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new BackgroundTask().execute();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background tasks here
            // This runs on a background thread

            // Example: Simulate a time-consuming operation
            try {
                Thread.sleep(1000); // Simulating a 5-second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //loadAndCacheImages();
            // Add your background tasks here, such as loading data or performing computations

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // This runs on the UI thread after background tasks are complete
            // Start the main activity or perform any necessary actions

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the splash activity to prevent going back to it
        }
    }

}
