package com.example.foliagefixer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Scan implements Parcelable {
    private int id;
    private String image;
    private int classification_id;
    private Classification classification;
    private float severity;
    private String solution;

    public Scan(int id, String image, int classification_id, Classification classification, float severity, String solutions) {
        this.id = id;
        this.image = image;
        this.classification_id = classification_id;
        this.classification = classification;
        this.severity = severity;
        this.solution = solutions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getClassification_id() {
        return classification_id;
    }

    public void setClassification_id(int classification_id) {
        this.classification_id = classification_id;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public float getSeverity() {
        return severity;
    }

    public void setSeverity(float severity) {
        this.severity = severity;
    }

    public String getSolutions() {
        return solution;
    }

    public void setSolutions(String solutions) {
        this.solution = solutions;
    }
    protected Scan(Parcel in) {
        id = in.readInt();
        image = in.readString();
        classification_id = in.readInt();
        classification = in.readParcelable(Classification.class.getClassLoader());
        severity = in.readFloat();
        solution = in.readString();
    }

    public static final Creator<Scan> CREATOR = new Creator<Scan>() {
        @Override
        public Scan createFromParcel(Parcel in) {
            return new Scan(in);
        }

        @Override
        public Scan[] newArray(int size) {
            return new Scan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(image);
        dest.writeInt(classification_id);
        dest.writeParcelable(classification, flags);
        dest.writeFloat(severity);
        dest.writeString(solution);
    }
}

