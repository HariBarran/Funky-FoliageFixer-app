package com.example.foliagefixer;
import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap image;
    private String title;
    private String imagePath;

    public ImageItem(Bitmap image, String title, String imagePath) {
        this.image = image;
        this.title = title;
        this.imagePath = imagePath;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}