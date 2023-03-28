package com.example.foliagefixer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class leafDiagnosis extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaf_diagnosis);
        image = (ImageView) findViewById(R.id.imageView);

        Bitmap dest = (Bitmap) getIntent().getParcelableExtra("leaf");
        image.setImageBitmap(dest);
    }
}