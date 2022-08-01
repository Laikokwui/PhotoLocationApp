package com.example.phototomap;

import android.graphics.Bitmap;

public class MyLocation {
    private int id;
    private double latitude;
    private double longitude;
    private Bitmap bitmap;

    public MyLocation(int id, double latitude, double longitude, Bitmap bitmap) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bitmap = bitmap;
    }

    public MyLocation(double latitude, double longitude, Bitmap bitmap) {
        this.id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
