package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraXActivity extends AppCompatActivity {
    private static final String TAG = "CameraXActivity";
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Uri imageUri;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        previewView = findViewById(R.id.preview_view);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error initializing CameraX", e);
            }
        }, ContextCompat.getMainExecutor(this));

        findViewById(R.id.capture_button).setOnClickListener(view -> takePicture());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving image...");
        progressDialog.setMessage("Please wait while the image is being saved.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
    }

    private void takePicture() {
        if (imageCapture == null) {
            return;
        }

        String imageFileName = generateImageFileName();

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), imageFileName)).build();
        progressDialog.show();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                progressDialog.dismiss();
                imageUri = outputFileResults.getSavedUri();

                if (imageUri == null) {
                    Log.e(TAG, "Failed to obtain saved image Uri");
                    return;
                }

                Log.d(TAG, "Image saved successfully, Uri: " + imageUri);

                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), imageUri.getPath(), imageFileName, "Image captured using FoliageFixer");
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found while inserting image into MediaStore", e);
                }

                Intent intent = new Intent(CameraXActivity.this, ImageDetailsActivity.class);
                intent.putExtra("image_uri", imageUri.toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                progressDialog.dismiss();
                Log.e(TAG, "Image capture error", exception);
                Toast.makeText(CameraXActivity.this, "Image capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private String generateImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "FF_" + timeStamp + ".jpg";
    }
}







