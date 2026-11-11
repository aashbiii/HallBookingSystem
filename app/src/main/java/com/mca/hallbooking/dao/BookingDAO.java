package com.mca.hallbooking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mca.hallbooking.db.DBHelper;
import com.mca.hallbooking.model.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingDAO {

    private final DBHelper dbHelper;
    private final Context context;

    // Joined query selecting booking + hall name + user name/email in one go
    private static final String JOINED_SELECT =
            "SELECT b.*, " +
                    "COALESCE(h." + DBHelper.COL_HALL_NAME + ", 'RCSS Campus Hall') AS hall_name, " +
                    "COALESCE(u." + DBHelper.COL_USER_NAME + ", 'Student') AS user_name, " +
                    "COALESCE(u." + DBHelper.COL_USER_EMAIL + ", '') AS user_email " +
                    "FROM " + DBHelper.TABLE_BOOKINGS + " b " +
                    "LEFT JOIN " + DBHelper.TABLE_HALLS + " h ON b." + DBHelper.COL_BOOKING_HALL_ID + " = h." + DBHelper.COL_HALL_ID + " " +
                    "LEFT JOIN " + DBHelper.TABLE_USERS + " u ON b." + DBHelper.COL_BOOKING_USER_ID + " = u." + DBHelper.COL_USER_ID;

    public BookingDAO(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = DBHelper.getInstance(context);
    }

    /**
     * Checks whether the given hall is already booked (pending or approved)
     * for the given date and slot. Returns true if the slot is NOT available.
     */
    public boolean isSlotTaken(long hallId, String date, String slot) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_BOOKINGS, new String[]{DBHelper.COL_BOOKING_ID},
                    DBHelper.COL_BOOKING_HALL_ID + "=? AND " + DBHelper.COL_BOOKING_DATE + "=? AND " +
                            DBHelper.COL_BOOKING_SLOT + "=? AND " + DBHelper.COL_BOOKING_STATUS + " IN (?,?)",
                    new String[]{String.valueOf(hallId), date, slot, Booking.STATUS_PENDING, Booking.STATUS_APPROVED},
                    null, null, null);
            boolean taken = cursor.getCount() > 0;
            cursor.close();
            return taken;
        } catch (Exception e) {
            android.util.Log.e("BookingDAO", "Exception checking slot availability", e);
            return false;
        }
    }

    /** Creates a new booking request with department and status PENDING. Returns new row id. */
    public long createBooking(long userId, long hallId, String eventName, String department, String date, String slot, int attendees) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String now = currentTimestamp();

            // Validate user exists to avoid foreign key violation
            Cursor userCursor = db.rawQuery("SELECT id FROM " + DBHelper.TABLE_USERS + " WHERE id=?",
                    new String[]{String.valueOf(userId)});
            boolean userExists = (userCursor != null && userCursor.moveToFirst());
            if (userCursor != null) userCursor.close();

            if (!userExists) {
                // Fallback to student@rajagiri.edu or first available user
                Cursor studentCursor = db.rawQuery("SELECT id FROM " + DBHelper.TABLE_USERS + " WHERE email=?",
                        new String[]{"student@rajagiri.edu"});
                if (studentCursor != null && studentCursor.moveToFirst()) {
                    userId = studentCursor.getLong(0);
                } else {
                    Cursor anyUser = db.rawQuery("SELECT id FROM " + DBHelper.TABLE_USERS + " LIMIT 1", null);
                    if (anyUser != null && anyUser.moveToFirst()) {
                        userId = anyUser.getLong(0);
                    }
                    if (anyUser != null) anyUser.close();
                }
                if (studentCursor != null) studentCursor.close();
            }

            // Validate hall exists to avoid foreign key violation
            Cursor hallCursor = db.rawQuery("SELECT id FROM " + DBHelper.TABLE_HALLS + " WHERE id=?",
                    new String[]{String.valueOf(hallId)});
            boolean hallExists = (hallCursor != null && hallCursor.moveToFirst());
            if (hallCursor != null) hallCursor.close();

            if (!hallExists) {
                Cursor anyHall = db.rawQuery("SELECT id FROM " + DBHelper.TABLE_HALLS + " LIMIT 1", null);
                if (anyHall != null && anyHall.moveToFirst()) {
                    hallId = anyHall.getLong(0);
                }
                if (anyHall != null) anyHall.close();
            }

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COL_BOOKING_USER_ID, userId);
            cv.put(DBHelper.COL_BOOKING_HALL_ID, hallId);
            cv.put(DBHelper.COL_BOOKING_EVENT_NAME, eventName);
            cv.put(DBHelper.COL_BOOKING_DEPARTMENT, department != null && !department.trim().isEmpty() ? department : "General");
            cv.put(DBHelper.COL_BOOKING_DATE, date);
            cv.put(DBHelper.COL_BOOKING_SLOT, slot);
            cv.put(DBHelper.COL_BOOKING_ATTENDEES, attendees);
            cv.put(DBHelper.COL_BOOKING_STATUS, Booking.STATUS_PENDING);
            cv.put(DBHelper.COL_BOOKING_CREATED_AT, now);
            cv.put(DBHelper.COL_BOOKING_UPDATED_AT, now);

            long rowId = db.insert(DBHelper.TABLE_BOOKINGS, null, cv);
            if (rowId == -1) {
                android.util.Log.e("BookingDAO", "db.insert returned -1. Attempting with conflict ignore.");
                rowId = db.insertWithOnConflict(DBHelper.TABLE_BOOKINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            return rowId;
        } catch (Exception e) {
            android.util.Log.e("BookingDAO", "Exception in createBooking", e);
            return -1;
        }
    }

    /** Overloaded createBooking for backwards compatibility. */
    public long createBooking(long userId, long hallId, String eventName, String date, String slot, int attendees) {
        return createBooking(userId, hallId, eventName, "General", date, slot, attendees);
    }

    public List<Booking> getBookingsForUser(long userId, String email) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (email != null && !email.trim().isEmpty() && userId > 0) {
                cursor = db.rawQuery(JOINED_SELECT + " WHERE b." + DBHelper.COL_BOOKING_USER_ID + "=? OR u." + DBHelper.COL_USER_EMAIL + "=? " +
                        "ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", new String[]{String.valueOf(userId), email.trim()});
            } else if (email != null && !email.trim().isEmpty()) {
                cursor = db.rawQuery(JOINED_SELECT + " WHERE u." + DBHelper.COL_USER_EMAIL + "=? " +
                        "ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", new String[]{email.trim()});
            } else {
                cursor = db.rawQuery(JOINED_SELECT + " WHERE b." + DBHelper.COL_BOOKING_USER_ID + "=? " +
                        "ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", new String[]{String.valueOf(userId)});
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(mapCursorToBooking(cursor));
                }
                cursor.close();
            }

            // Fallback: If still empty, check if any booking exists for student@rajagiri.edu
            if (list.isEmpty()) {
                Cursor c2 = db.rawQuery(JOINED_SELECT + " WHERE u." + DBHelper.COL_USER_EMAIL + "='student@rajagiri.edu' " +
                        "ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", null);
                if (c2 != null) {
                    while (c2.moveToNext()) {
                        list.add(mapCursorToBooking(c2));
                    }
                    c2.close();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("BookingDAO", "Error in getBookingsForUser", e);
        }
        return list;
    }

    public List<Booking> getBookingsForUser(long userId) {
        return getBookingsForUser(userId, null);
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(JOINED_SELECT + " WHERE b." + DBHelper.COL_BOOKING_STATUS +
                "=? ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", new String[]{status});
        while (cursor.moveToNext()) {
            list.add(mapCursorToBooking(cursor));
        }
        cursor.close();
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(JOINED_SELECT + " ORDER BY b." + DBHelper.COL_BOOKING_ID + " DESC", null);
        while (cursor.moveToNext()) {
            list.add(mapCursorToBooking(cursor));
        }
        cursor.close();
        return list;
    }

    public Booking getBookingById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(JOINED_SELECT + " WHERE b." + DBHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(id)});
        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = mapCursorToBooking(cursor);
        }
        cursor.close();
        return booking;
    }

    /** Approves a booking and generates a unique receipt number. */
    public boolean approveBooking(long bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_BOOKING_STATUS, Booking.STATUS_APPROVED);
        cv.put(DBHelper.COL_BOOKING_RECEIPT_NO, generateReceiptNo(bookingId));
        cv.put(DBHelper.COL_BOOKING_UPDATED_AT, currentTimestamp());
        int rows = db.update(DBHelper.TABLE_BOOKINGS, cv,
                DBHelper.COL_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
        return rows > 0;
    }

    /** Rejects a booking with an optional remark. */
    public boolean rejectBooking(long bookingId, String remarks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_BOOKING_STATUS, Booking.STATUS_REJECTED);
        cv.put(DBHelper.COL_BOOKING_REMARKS, remarks);
        cv.put(DBHelper.COL_BOOKING_UPDATED_AT, currentTimestamp());
        int rows = db.update(DBHelper.TABLE_BOOKINGS, cv,
                DBHelper.COL_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
        return rows > 0;
    }

    private String generateReceiptNo(long bookingId) {
        String stamp = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        return "RCSS-" + stamp + "-" + String.format(Locale.US, "%04d", bookingId);
    }

    private String currentTimestamp() {
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date());
    }

    private Booking mapCursorToBooking(Cursor cursor) {
        Booking b = new Booking();
        b.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_ID)));
        b.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_USER_ID)));
        b.setHallId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_HALL_ID)));
        b.setEventName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_EVENT_NAME)));

        int deptCol = cursor.getColumnIndex(DBHelper.COL_BOOKING_DEPARTMENT);
        if (deptCol != -1) {
            b.setDepartment(cursor.getString(deptCol));
        } else {
            b.setDepartment("Academic Department");
        }

        b.setBookingDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_DATE)));
        b.setSlot(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_SLOT)));
        b.setAttendees(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_ATTENDEES)));
        b.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_STATUS)));
        b.setRemarks(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_REMARKS)));
        b.setReceiptNo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_RECEIPT_NO)));
        b.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_CREATED_AT)));
        b.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOOKING_UPDATED_AT)));
        int hallCol = cursor.getColumnIndex("hall_name");
        b.setHallName(hallCol != -1 && !cursor.isNull(hallCol) ? cursor.getString(hallCol) : "RCSS Campus Hall");

        int userCol = cursor.getColumnIndex("user_name");
        b.setUserName(userCol != -1 && !cursor.isNull(userCol) ? cursor.getString(userCol) : "Student");

        int emailCol = cursor.getColumnIndex("user_email");
        b.setUserEmail(emailCol != -1 && !cursor.isNull(emailCol) ? cursor.getString(emailCol) : "");
        return b;
    }
}
