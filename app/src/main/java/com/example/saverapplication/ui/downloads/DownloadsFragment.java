package com.example.saverapplication.ui.downloads;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saverapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment {

    private DownloadsAdapter downloadsAdapter;

    public DownloadsFragment() {
    }

    public static DownloadsFragment newInstance() {
        return new DownloadsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        int numberOfColumns = 2;

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumns));

        downloadsAdapter = new DownloadsAdapter(requireContext(), new ArrayList<>(), new DownloadsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DownloadedItem downloadedItem) {
            }

            @Override
            public void onShareClick(DownloadedItem downloadedItem) {
                shareImage(downloadedItem.getFilePath());
            }
        });

        recyclerView.setAdapter(downloadsAdapter);

        loadDownloadedItemsAsync();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("DownloadsFragment", "Fragment resumed, refreshing data...");
        loadDownloadedItemsAsync();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadDownloadedItemsAsync() {
        new AsyncTask<Void, Void, List<DownloadedItem>>() {
            @Override
            protected List<DownloadedItem> doInBackground(Void... voids) {
                Log.d("DownloadsFragment", "Loading downloaded items in background...");
                return getDownloadedItems();
            }

            @Override
            protected void onPostExecute(List<DownloadedItem> downloadedItems) {
                Log.d("DownloadsFragment", "Loaded " + downloadedItems.size() + " items");

                downloadsAdapter.setData(downloadedItems);
            }
        }.execute();
    }

    public void refreshData() {
        Log.d("DownloadsFragment", "Refreshing data...");
        loadDownloadedItemsAsync();
    }

    private List<DownloadedItem> getDownloadedItems() {

        File internalStatusSaverDirectory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "StatusSaver");
        List<DownloadedItem> downloadedItems = new ArrayList<>(getDownloadedItemsFromDirectory(internalStatusSaverDirectory, true));

        File externalDcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File externalStatusSaverDirectory = new File(externalDcimDirectory, "StatusSaver");
        downloadedItems.addAll(getDownloadedItemsFromDirectory(externalStatusSaverDirectory, false));

        Log.d("DownloadsFragment", "Found " + downloadedItems.size() + " downloaded items.");
        return downloadedItems;
    }

    private List<DownloadedItem> getDownloadedItemsFromDirectory(File directory, boolean isInternal) {
        List<DownloadedItem> downloadedItems = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    String thumbnailUri = getThumbnailUriForFile(file);
                    DownloadedItem downloadedItem = new DownloadedItem(file.getAbsolutePath(), file.getName(), thumbnailUri, isInternal);
                    downloadedItems.add(downloadedItem);
                }
            }
        } else {
            Log.e("DownloadsFragment", "StatusSaver directory not found: " + directory.getAbsolutePath());
        }

        return downloadedItems;
    }

    private String getThumbnailUriForFile(File file) {
        String mediaType = determineMediaType(file);

        if ("image".equals(mediaType)) {
            return getImageThumbnailUriForFile(file.getPath());
        } else if ("video".equals(mediaType)) {
            return generateVideoThumbnailUri(file);
        }

        return null;
    }

    private String generateVideoThumbnailUri(File videoFile) {
        MediaMetadataRetriever retriever = null;

        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());
            Bitmap thumbnail = retriever.getFrameAtTime(500000);

            if (thumbnail != null) {
                File thumbnailFile = saveThumbnailToDisk(thumbnail, videoFile.getName());
                return Uri.fromFile(thumbnailFile).toString();
            }
        } finally {
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private String getImageThumbnailUriForFile(String filePath) {
        try {
            Bitmap thumbnailBitmap = decodeSampledBitmapFromFile(filePath);

            if (thumbnailBitmap != null) {
                File thumbnailFile = saveThumbnailToDisk(thumbnailBitmap, "thumbnail_" + new File(filePath).getName());
                return Uri.fromFile(thumbnailFile).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.fromFile(new File(filePath)).toString();
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 100 || width > 100) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= 100 && (halfWidth / inSampleSize) >= 100) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String determineMediaType(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            return "image";
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".mkv") || fileName.endsWith(".avi")) {
            return "video";
        }

        return "unknown";
    }

    private File saveThumbnailToDisk(Bitmap thumbnail, String fileName) {
        File thumbnailFile = new File(requireContext().getCacheDir(), "thumbnail_" + fileName + ".jpg");

        try (FileOutputStream out = new FileOutputStream(thumbnailFile)) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumbnailFile;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void shareImage(String imageUri) {
        File imageFile = new File(Uri.parse(imageUri).getPath());

        Uri contentUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getApplicationContext().getPackageName() + ".provider",
                imageFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Open image with"));
        } else {
            Toast.makeText(requireContext(), "No app found to open the image", Toast.LENGTH_SHORT).show();
        }
    }
}
