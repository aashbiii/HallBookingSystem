package com.mca.hallbooking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mca.hallbooking.db.DBHelper;
import com.mca.hallbooking.model.User;

public class UserDAO {

    private final DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    /** Returns true if the email is already registered. */
    public boolean emailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USERS,
                new String[]{DBHelper.COL_USER_ID},
                DBHelper.COL_USER_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /** Registers a new USER account. Returns the new row id, or -1 on failure. */
    public long registerUser(String name, String email, String phone, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_USER_NAME, name);
        cv.put(DBHelper.COL_USER_EMAIL, email);
        cv.put(DBHelper.COL_USER_PHONE, phone);
        cv.put(DBHelper.COL_USER_PASSWORD, password);
        cv.put(DBHelper.COL_USER_ROLE, User.ROLE_USER);
        return db.insert(DBHelper.TABLE_USERS, null, cv);
    }

    /** Validates credentials and returns the matching User, or null if invalid. */
    public User login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USERS, null,
                DBHelper.COL_USER_EMAIL + "=? AND " + DBHelper.COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = mapCursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USERS, null,
                DBHelper.COL_USER_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = mapCursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USERS, null,
                DBHelper.COL_USER_EMAIL + "=?", new String[]{email},
                null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = mapCursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean updateProfile(long userId, String name, String phone, String department, String profileImage) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_USER_NAME, name);
        cv.put(DBHelper.COL_USER_PHONE, phone);
        if (department != null) {
            cv.put(DBHelper.COL_USER_DEPARTMENT, department);
        }
        if (profileImage != null) {
            cv.put(DBHelper.COL_USER_IMAGE, profileImage);
        }
        return db.update(DBHelper.TABLE_USERS, cv, DBHelper.COL_USER_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean updatePassword(long userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_USER_PASSWORD, newPassword);
        return db.update(DBHelper.TABLE_USERS, cv, DBHelper.COL_USER_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    private User mapCursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_EMAIL)));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PHONE)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PASSWORD)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ROLE)));

        int imgCol = cursor.getColumnIndex(DBHelper.COL_USER_IMAGE);
        if (imgCol != -1) {
            user.setProfileImage(cursor.getString(imgCol));
        }

        int deptCol = cursor.getColumnIndex(DBHelper.COL_USER_DEPARTMENT);
        if (deptCol != -1) {
            user.setDepartment(cursor.getString(deptCol));
        }

        return user;
    }
}
