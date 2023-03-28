package com.example.foliagefixer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recent extends AppCompatActivity {

    private List<ImageItem> imageItems;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        listView = findViewById(R.id.listView);
        imageItems = new ArrayList<>();

        loadImagesFromFolder();

        CustomListAdapter adapter = new CustomListAdapter(this, imageItems);
        listView.setAdapter(adapter);

        // Add a click listener for the ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem item = imageItems.get(position);

                // Pass the image and text to a new activity, for example, to display the image in a larger view
                Intent intent = new Intent(Recent.this, ImageDetailsActivity.class);
                intent.putExtra("image_path", item.getImagePath());
                intent.putExtra("title", item.getTitle());
                startActivity(intent);
            }
        });
    }

    private void loadImagesFromFolder() {
        File folder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file)) {
                        String title = file.getName();
                        title = title.substring(0, title.lastIndexOf('.'));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        imageItems.add(new ImageItem(BitmapFactory.decodeFile(file.getAbsolutePath(), options), title, file.getAbsolutePath()));
                    }
                }
            }
        }
    }

    private boolean isImageFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp");
    }
}
