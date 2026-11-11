package com.mca.hallbooking;

import android.app.Application;

import com.mca.hallbooking.db.DBHelper;

public class HallBookingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Warm up / create the database as soon as the app starts.
        DBHelper.getInstance(this).getWritableDatabase();
    }
}
