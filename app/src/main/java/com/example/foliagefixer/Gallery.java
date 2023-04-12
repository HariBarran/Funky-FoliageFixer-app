package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 101;
    private ArrayList<ImageItem> imageItems;
    private static final int REQUEST_PICK_IMAGE = 1;
    private GalleryAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        requestStoragePermission();

        TextView logoutTextView = findViewById(R.id.logoutTextView);
        mAuth = FirebaseAuth.getInstance();
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Auth
                mAuth.signOut();

                // Navigate back to the login screen or any other appropriate activity
                Intent intent = new Intent(Gallery.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        Button importGalleryButton = findViewById(R.id.import_gallery_button);
        importGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        imageItems = new ArrayList<>();

        loadImages();

        TextView appTitle = findViewById(R.id.Title1);
        appTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gallery.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        adapter = new GalleryAdapter(this, imageItems, new GalleryAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(ImageItem imageItem) {
                Log.d("GALLERY", "Image Uri: " + imageItem.getImageUri().toString());
                Intent intent = new Intent(Gallery.this, ImageDetailsActivity.class);
                intent.putExtra("image_uri", imageItem.getImageUri().toString());
                intent.putExtra("title", imageItem.getTitle());
                startActivity(intent);
            }
        }, new GalleryAdapter.OnImageLongClickListener() {
            @Override
            public void onImageLongClick(ImageItem imageItem, int position) {
                deleteImage(imageItem, position);
            }
        });


        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }

    private void loadImages() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME},
                MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?",
                new String[]{"FF_%"},
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            imageItems.clear();
            while (cursor.moveToNext()) {
                long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                ImageItem imageItem = new ImageItem(imageUri, imageName, imageId);
                imageItems.add(imageItem);
            }
            cursor.close();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                saveImportedImage(selectedImageUri);
            }
        }
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();
            } else {
                Toast.makeText(this, "Permission denied. Please grant the necessary permission to access the gallery.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImportedImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            String fileName = "FF_" + System.currentTimeMillis() + ".jpg";
            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File destinationFile = new File(storageDirectory, fileName);
            OutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Insert the saved image into the MediaStore
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri newImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try (OutputStream newImageOutputStream = getContentResolver().openOutputStream(newImageUri)) {
                Bitmap bitmap = BitmapFactory.decodeFile(destinationFile.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, newImageOutputStream);
            } catch (IOException e) {
                Log.e("Gallery", "Failed to insert image into MediaStore", e);
            }

            contentValues.clear();
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);
            getContentResolver().update(newImageUri, contentValues, null, null);

            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save imported image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImage(ImageItem imageItem, int position) {
        // Get the file path from the imageUri
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageItem.getImageUri(), filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            // Delete the image file from the app's specific storage directory
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                    // Remove the image from the imageItems list and update the adapter
                    imageItems.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, imageItems.size());
                } else {
                    Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}

