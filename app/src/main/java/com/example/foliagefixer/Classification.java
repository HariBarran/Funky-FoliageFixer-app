package com.example.foliagefixer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Classification implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("classification")
    private String name;

    // Add getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    // Implement Parcelable interface

    protected Classification(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Classification> CREATOR = new Creator<Classification>() {
        @Override
        public Classification createFromParcel(Parcel in) {
            return new Classification(in);
        }

        @Override
        public Classification[] newArray(int size) {
            return new Classification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}


