package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 101;
    public static final int CAMERA_PERM_CODE = REQUEST_CODE;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseAuth mAuth;
    String currentPhotoPath;
    private ImageView image;
    private CardView gallerycard, cameracard, historycard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gallerycard = (CardView) findViewById(R.id.gallerycard);
        cameracard = (CardView) findViewById(R.id.historycard);
        historycard = (CardView) findViewById(R.id.cameracard);
        image = (ImageView) findViewById(R.id.logoimage);

        gallerycard.setOnClickListener(this);
        cameracard.setOnClickListener(this);
        historycard.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cameracard:
                getPermission();
                break;
            case R.id.gallerycard:

                break;
            case R.id.historycard:
                startActivity(new Intent(this, Recent.class));
                break;
        }

    }

    private void getPermission(){
        if((ContextCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
        ((ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED))) ){
            ActivityCompat.requestPermissions(Home.this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        }
        else{
//            openCamera();
            dispatchTakePictureIntent();
        }

    }

//    private void openCamera() {
////        Toast.makeText(this, "Camera Open Request", Toast.LENGTH_SHORT).show();
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStorageDirectory(), "MyImages");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.foliagefixer.fileprovider",
                        photoFile);
//                Toast.makeText(this, "qwdqwdqwdqwdCamera Open Request", Toast.LENGTH_SHORT).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        File f = null;
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                f = new File (currentPhotoPath);
                if (f != null){
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);
                    Toast.makeText(this, currentPhotoPath, Toast.LENGTH_SHORT).show();
                    Log.d("tag", "Url of image is " + contentUri.fromFile(f));

                    Intent intent = new Intent(this, Recent.class);
                    intent.putExtra("image_path", currentPhotoPath);
                    startActivity(intent);

                }


            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}