package com.mca.hallbooking.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mca.hallbooking.db.DBHelper;
import com.mca.hallbooking.model.Hall;

import java.util.ArrayList;
import java.util.List;

public class HallDAO {

    private final DBHelper dbHelper;

    public HallDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public List<Hall> getAllHalls() {
        List<Hall> halls = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_HALLS, null, null, null,
                null, null, DBHelper.COL_HALL_NAME + " ASC");

        while (cursor.moveToNext()) {
            halls.add(mapCursorToHall(cursor));
        }
        cursor.close();
        return halls;
    }

    public Hall getHallById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_HALLS, null,
                DBHelper.COL_HALL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Hall hall = null;
        if (cursor.moveToFirst()) {
            hall = mapCursorToHall(cursor);
        }
        cursor.close();
        return hall;
    }

    private Hall mapCursorToHall(Cursor cursor) {
        Hall hall = new Hall();
        hall.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_ID)));
        hall.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_NAME)));
        hall.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_LOCATION)));
        hall.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_CAPACITY)));
        hall.setAmenities(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_AMENITIES)));
        hall.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_PRICE)));
        hall.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_HALL_DESCRIPTION)));
        
        int imgIndex = cursor.getColumnIndex(DBHelper.COL_HALL_IMAGE_URIS);
        if (imgIndex != -1) {
            hall.setImageUris(cursor.getString(imgIndex));
        }
        
        return hall;
    }
    
    public boolean updateHall(Hall hall) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put(DBHelper.COL_HALL_NAME, hall.getName());
        cv.put(DBHelper.COL_HALL_LOCATION, hall.getLocation());
        cv.put(DBHelper.COL_HALL_CAPACITY, hall.getCapacity());
        cv.put(DBHelper.COL_HALL_AMENITIES, hall.getAmenities());
        cv.put(DBHelper.COL_HALL_PRICE, hall.getPrice());
        cv.put(DBHelper.COL_HALL_DESCRIPTION, hall.getDescription());
        cv.put(DBHelper.COL_HALL_IMAGE_URIS, hall.getImageUris());
        
        int result = db.update(DBHelper.TABLE_HALLS, cv, DBHelper.COL_HALL_ID + "=?", new String[]{String.valueOf(hall.getId())});
        return result > 0;
    }

    public long insertHall(Hall hall) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put(DBHelper.COL_HALL_NAME, hall.getName());
        cv.put(DBHelper.COL_HALL_LOCATION, hall.getLocation());
        cv.put(DBHelper.COL_HALL_CAPACITY, hall.getCapacity());
        cv.put(DBHelper.COL_HALL_AMENITIES, hall.getAmenities());
        cv.put(DBHelper.COL_HALL_PRICE, hall.getPrice());
        cv.put(DBHelper.COL_HALL_DESCRIPTION, hall.getDescription());
        cv.put(DBHelper.COL_HALL_IMAGE_URIS, hall.getImageUris());

        return db.insert(DBHelper.TABLE_HALLS, null, cv);
    }

    public boolean deleteHall(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DBHelper.TABLE_HALLS, DBHelper.COL_HALL_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
