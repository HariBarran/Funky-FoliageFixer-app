package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class Home extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_CAMERA_AND_STORAGE_PERMS = 124;
    private FirebaseAuth mAuth;
    private ImageView image;
    private CardView gallerycard, cameracard, historycard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Find the toolbar in the layout
        mAuth = FirebaseAuth.getInstance();
        TextView logoutTextView = findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Auth
                mAuth.signOut();

                // Navigate back to the login screen or any other appropriate activity
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        // Set the custom toolbar as the ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        gallerycard = (CardView) findViewById(R.id.gallerycard);
        cameracard = (CardView) findViewById(R.id.cameracard);
        historycard = (CardView) findViewById(R.id.historycard);
        image = (ImageView) findViewById(R.id.logoImageView);


        gallerycard.setOnClickListener(this);
        cameracard.setOnClickListener(this);
        historycard.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cameracard:
                getPermission();
                break;
            case R.id.gallerycard:
                startActivity(new Intent(this, Gallery.class));
                break;
            case R.id.historycard:
                startActivity(new Intent(this, Recent.class));
                break;
        }
    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE_PERMS)
    private void getPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivity(new Intent(this, CameraXActivity.class));
        } else {
            EasyPermissions.requestPermissions(this, "We need camera and storage permissions to capture and save images", RC_CAMERA_AND_STORAGE_PERMS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
