package com.example.saverapplication.ui.whatsapp4b;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.downloads.ImageViewerActivity;
import com.example.saverapplication.ui.whatsapp.images.ImageData;
import com.example.saverapplication.ui.whatsapp.images.ImagesAdapter;

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

public class ImageBFragment extends Fragment {
    private ImagesAdapter imagesAdapter;
    private static final int REQUEST_CODE = 100;

    public ImageBFragment() {
    }

    public static ImageBFragment newInstance() {
        return new ImageBFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_b, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.wb_recycler_view_images);
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

        imagesAdapter = new ImagesAdapter(requireContext(), new ArrayList<>(), itemClickListener);
        recyclerView.setAdapter(imagesAdapter);

        // Check for permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            loadImages();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImages();
    }

    private void loadImages() {
        imagesAdapter.updateImageData(getImageData());
    }

    private void openImage(String imageUri) {
        Log.d("ImageUri", imageUri);

        if (imageUri == null || imageUri.isEmpty()) {
            Log.e("ImageBFragment", "Image URI is invalid");
            return;
        }

        String filePath = Uri.parse(imageUri).getPath();
        Log.d("ImageBFragment", "Checking file path: " + filePath);

        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            Log.e("ImageBFragment", "Image file does not exist: " + filePath);
            return;
        }

        Intent intent = new Intent(requireContext(), ImageViewerActivity.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages(); // Load images if permission is granted
            } else {
                Toast.makeText(requireContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private List<ImageData> getImageData() {
        List<ImageData> imageDataList = new ArrayList<>();

        File whatsappBusinessDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses");
        if (whatsappBusinessDirectory.exists() && whatsappBusinessDirectory.isDirectory()) {
            File[] files = whatsappBusinessDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file)) {
                        Uri imageUri = Uri.fromFile(file);
                        Uri thumbnailUri = getThumbnailUriForImage(file);

                        ImageData imageData = new ImageData(imageUri.toString(), thumbnailUri.toString());
                        imageDataList.add(imageData);
                    }
                }
            }
        } else {
            Log.e("ImagesFragment", "WhatsApp Business Status directory not found.");
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
            e.printStackTrace();
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
        try {
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnailFile;
    }

    private void downloadImage(String imageUri) {
        Uri uri = Uri.parse(imageUri);

        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        if (!statusSaverDirectory.exists()) {
            statusSaverDirectory.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "image_" + timeStamp + "." + getFileExtension(uri);

        File destinationFile = new File(statusSaverDirectory, fileName);

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            OutputStream outputStream = Files.newOutputStream(destinationFile.toPath());

            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(destinationFile));
            requireContext().sendBroadcast(mediaScanIntent);

            Toast.makeText(requireContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        return (extension != null && !extension.isEmpty()) ? extension : "jpg";
    }
}

