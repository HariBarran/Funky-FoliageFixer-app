package com.example.foliagefixer;

public class MyResponseObject {
    private double severity;
    private String classification;
    private int classificationId;
    private String solutions;
    private String imageUrl;

    // Getters
    public double getSeverity() {
        return severity;
    }

    public String getClassification() {
        return classification;
    }

    public int getClassificationId() {
        return classificationId;
    }

    public String getSolutions() {
        return solutions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setSeverity(double severity) {
        this.severity = severity;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }

    public void setSolutions(String solutions) {
        this.solutions = solutions;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}





