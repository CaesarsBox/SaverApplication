package com.example.saverapplication.ui.whatsapp.images;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saverapplication.R;
import com.example.saverapplication.ui.downloads.ImageViewerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagesFragment extends Fragment {
    public ImagesFragment() {
        // Required empty public constructor
    }

    public static ImagesFragment newInstance() {
        return new ImagesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_images);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        ImagesAdapter.OnItemClickListener itemClickListener = new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageUri) {
                openImage(imageUri);
            }

            @Override
            public void onDownloadClick(String imageUri) {
                downloadImage(imageUri);
            }
        };

        ImagesAdapter imagesAdapter = new ImagesAdapter(requireContext(), getImageData(), itemClickListener);
        recyclerView.setAdapter(imagesAdapter);

        return view;
    }

    private List<ImageData> getImageData() {
        List<ImageData> imageDataList = new ArrayList<>();

        File whatsappStatusDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses");
        imageDataList.addAll(getImagesFromDirectory(whatsappStatusDirectory));

        Log.d("ImageDataListSize", "Number of images found: " + imageDataList.size());
        return imageDataList;
    }


    private List<ImageData> getImagesFromDirectory(File directory) {
        List<ImageData> imageDataList = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !file.isHidden() && isImageFile(file)) {
                        Uri imageUri = Uri.fromFile(file);
                        Uri thumbnailUri = getThumbnailUriForImage(file);
                        imageDataList.add(new ImageData(imageUri.toString(), thumbnailUri.toString()));
                    }
                }
            }
        } else {
            Log.e("ImagesFragment", directory.getAbsolutePath() + " not found.");
        }
        return imageDataList;
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = new String[]{".jpg", ".png", ".jpeg"};
        String fileName = file.getName().toLowerCase();

        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private Uri getThumbnailUriForImage(File imageFile) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds = false;

            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            File thumbnailFile = saveThumbnailToFile(thumbnailBitmap, "thumbnail_" + imageFile.getName());

            return Uri.fromFile(thumbnailFile);
        } catch (Exception e) {
            Log.e("ImagesFragment", "Error generating thumbnail for " + imageFile.getName(), e);
            return Uri.fromFile(imageFile);
        }
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

    private File saveThumbnailToFile(Bitmap thumbnailBitmap, String fileName) {
        File thumbnailFile = new File(requireContext().getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            return thumbnailFile;
        } catch (IOException e) {
            Log.e("ImagesFragment", "Error saving thumbnail to file", e);
            return null;
        }
    }

    private void openImage(String imageUri) {
        Log.d("ImageUri", imageUri);

        Intent intent = new Intent(requireContext(), ImageViewerActivity.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
    }

    private void downloadImage(String imageUri) {
        Uri uri = Uri.parse(imageUri);

        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        if (!statusSaverDirectory.exists()) {
            statusSaverDirectory.mkdirs();
        }

        String fileName = "image_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "." + getFileExtension(uri);
        File destinationFile = new File(statusSaverDirectory, fileName);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = Files.newOutputStream(destinationFile.toPath())) {

            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }

            // Notify media scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(destinationFile));
            requireContext().sendBroadcast(mediaScanIntent);

            Toast.makeText(requireContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ImagesFragment", "Failed to download image", e);
            Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
        return (extension != null && !extension.isEmpty()) ? extension : "jpg";
    }
}
