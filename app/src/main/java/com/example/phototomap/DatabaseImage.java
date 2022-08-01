package com.example.phototomap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DatabaseImage extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocationDB";
    private static final String TABLE_NAME = "LOCATIONS";
    private static final String KEY_ID="ID";
    private static final String KEY_LATITUDE="LATITUDE";
    private static final String KEY_LONGITUDE="LONGITUDE";
    private static final String KEY_IMAGE="IMAGE";

    public DatabaseImage(@Nullable Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_LATITUDE + " DOUBLE, "
                + KEY_LONGITUDE + " DOUBLE, "
                + KEY_IMAGE + " BLOB"
                + ")";
        db.execSQL(query);
        System.out.println("Table LOCATIONS Successfully Created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        System.out.println("Table LOCATIONS Successfully Dropped");
    }

    public long AddALocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LATITUDE,location.getLatitude());
        contentValues.put(KEY_LONGITUDE,location.getLongitude());
        contentValues.put(KEY_IMAGE,getImage2Byte(location.getBitmap()));

        return db.insert(TABLE_NAME,null, contentValues);
    }

    public long UpdateLocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IMAGE, getImage2Byte(location.getBitmap()));
        return db.update(TABLE_NAME, contentValues, String.format("%s =?",KEY_ID), new String[]{String.valueOf(location.getId())});
    }

    public ArrayList<MyLocation> getAllLocations() {
        ArrayList<MyLocation> location_list = new ArrayList<>();
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            MyLocation location = new MyLocation(
                Integer.parseInt(cursor.getString(0)),
                cursor.getDouble(1),
                cursor.getDouble(2),
                getByte2Image(cursor.getBlob(3))
            );
            location_list.add(location);
        }
        return location_list;
    }

    public void RemoveAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

    private byte[] getImage2Byte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private Bitmap getByte2Image(byte[] byteImg) {
        return BitmapFactory.decodeByteArray(byteImg , 0, byteImg.length);
    }
}
