package com.example.foliagefixer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;



public class ImageItem {
    private Bitmap image;
    private String title;
    private Uri imageUri;
    private long timestamp;
    private String diseaseName;
    private String diseaseSeverity;
    private ArrayList<String> diseaseRecommendation;
    private int classificationID;


    public ImageItem(Bitmap image, String title, Uri imageUri, long timestamp, String diseaseName, String diseaseSeverity, ArrayList<String> diseaseRecommendation, int classificationID) {
        this.image = image;
        this.title = title;
        this.imageUri = imageUri;
        this.timestamp = timestamp;
        this.diseaseName = diseaseName;
        this.diseaseSeverity = diseaseSeverity;
        this.diseaseRecommendation = diseaseRecommendation;
        this.classificationID = classificationID;
    }
    public ImageItem(Uri imageUri, String title, long timestamp) {
        this(null, title, imageUri, timestamp, null, null, null, 0);
    }



    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getclassificationID() {
        return classificationID;
    }

    public void setclassificationID(Integer classificationID) {
        this.classificationID = classificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getImageUri(){
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseSeverity() {
        return diseaseSeverity;
    }

    public void setDiseaseSeverity(String diseaseSeverity) {
        this.diseaseSeverity = diseaseSeverity;
    }

    public ArrayList<String> getDiseaseRecommendation() {
        return diseaseRecommendation;
    }

    public void setDiseaseRecommendation(ArrayList diseaseRecommendation) {
        this.diseaseRecommendation = diseaseRecommendation;
    }

//    public static Bitmap getBitmapImageFromPath(String filePath) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
//        return bitmap;
//    }
//
//    public String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(byteArray, Base64.DEFAULT);
//    }
//
//    public Bitmap base64ToBitmap(String base64) {
//        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//    }
}
