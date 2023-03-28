package com.example.foliagefixer;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imageView = findViewById(R.id.detailsImageView);
        TextView textView = findViewById(R.id.detailsTextView);

        String imagePath = getIntent().getStringExtra("image_path");
        String title = getIntent().getStringExtra("title");

        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        textView.setText(title);
    }
}
