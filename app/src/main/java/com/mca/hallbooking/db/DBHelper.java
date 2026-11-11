package com.mca.hallbooking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Central SQLite database helper for the Hall Booking System.
 * Creates three tables: users, halls, bookings and seeds them with
 * a default manager account and a set of sample event halls so the
 * app is usable immediately after a fresh install.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hall_booking.db";
    private static final int DATABASE_VERSION = 9;

    // ---------- Table: users ----------
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";
    public static final String COL_USER_IMAGE = "profile_image";
    public static final String COL_USER_DEPARTMENT = "department";

    // ---------- Table: halls ----------
    public static final String TABLE_HALLS = "halls";
    public static final String COL_HALL_ID = "id";
    public static final String COL_HALL_NAME = "name";
    public static final String COL_HALL_LOCATION = "location";
    public static final String COL_HALL_CAPACITY = "capacity";
    public static final String COL_HALL_AMENITIES = "amenities";
    public static final String COL_HALL_PRICE = "price";
    public static final String COL_HALL_DESCRIPTION = "description";
    public static final String COL_HALL_IMAGE_URIS = "image_uris";

    // ---------- Table: bookings ----------
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_USER_ID = "user_id";
    public static final String COL_BOOKING_HALL_ID = "hall_id";
    public static final String COL_BOOKING_EVENT_NAME = "event_name";
    public static final String COL_BOOKING_DEPARTMENT = "department";
    public static final String COL_BOOKING_DATE = "booking_date";
    public static final String COL_BOOKING_SLOT = "slot";
    public static final String COL_BOOKING_ATTENDEES = "attendees";
    public static final String COL_BOOKING_STATUS = "status";
    public static final String COL_BOOKING_REMARKS = "remarks";
    public static final String COL_BOOKING_RECEIPT_NO = "receipt_no";
    public static final String COL_BOOKING_CREATED_AT = "created_at";
    public static final String COL_BOOKING_UPDATED_AT = "updated_at";

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        ensureUserProfileColumns(db);
        ensureDepartmentColumn(db);
        ensureDefaultUsers(db);
        ensureRCSSHalls(db);
    }

    private void ensureUserProfileColumns(SQLiteDatabase db) {
        try {
            boolean hasImage = false;
            boolean hasDept = false;
            android.database.Cursor c = db.rawQuery("PRAGMA table_info(" + TABLE_USERS + ")", null);
            if (c != null) {
                int nameCol = c.getColumnIndex("name");
                while (c.moveToNext()) {
                    String col = c.getString(nameCol);
                    if (COL_USER_IMAGE.equalsIgnoreCase(col)) hasImage = true;
                    if (COL_USER_DEPARTMENT.equalsIgnoreCase(col)) hasDept = true;
                }
                c.close();
            }
            if (!hasImage) {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_IMAGE + " TEXT");
            }
            if (!hasDept) {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_DEPARTMENT + " TEXT DEFAULT 'General'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureDepartmentColumn(SQLiteDatabase db) {
        try {
            boolean hasDept = false;
            android.database.Cursor c = db.rawQuery("PRAGMA table_info(" + TABLE_BOOKINGS + ")", null);
            if (c != null) {
                int nameCol = c.getColumnIndex("name");
                while (c.moveToNext()) {
                    if (COL_BOOKING_DEPARTMENT.equalsIgnoreCase(c.getString(nameCol))) {
                        hasDept = true;
                        break;
                    }
                }
                c.close();
            }
            if (!hasDept) {
                db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COL_BOOKING_DEPARTMENT + " TEXT NOT NULL DEFAULT 'General'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureDefaultUsers(SQLiteDatabase db) {
        try {
            android.database.Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "='admin@rajagiri.edu'", null);
            boolean hasAdmin = false;
            if (c != null) {
                if (c.moveToFirst() && c.getInt(0) > 0) hasAdmin = true;
                c.close();
            }
            if (!hasAdmin) {
                ContentValues manager = new ContentValues();
                manager.put(COL_USER_NAME, "RCSS Hall Administrator");
                manager.put(COL_USER_EMAIL, "admin@rajagiri.edu");
                manager.put(COL_USER_PHONE, "0484-2911111");
                manager.put(COL_USER_PASSWORD, "Admin@123");
                manager.put(COL_USER_ROLE, "MANAGER");
                db.insert(TABLE_USERS, null, manager);
            }

            c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "='student@rajagiri.edu'", null);
            boolean hasStudent = false;
            if (c != null) {
                if (c.moveToFirst() && c.getInt(0) > 0) hasStudent = true;
                c.close();
            }
            if (!hasStudent) {
                ContentValues demoUser = new ContentValues();
                demoUser.put(COL_USER_NAME, "Rajagiri Student");
                demoUser.put(COL_USER_EMAIL, "student@rajagiri.edu");
                demoUser.put(COL_USER_PHONE, "9876543210");
                demoUser.put(COL_USER_PASSWORD, "Student@123");
                demoUser.put(COL_USER_ROLE, "USER");
                db.insert(TABLE_USERS, null, demoUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureRCSSHalls(SQLiteDatabase db) {
        try {
            android.database.Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HALLS + " WHERE " + COL_HALL_NAME + "='Golden Auerole'", null);
            boolean hasGolden = false;
            if (c != null) {
                if (c.moveToFirst() && c.getInt(0) > 0) hasGolden = true;
                c.close();
            }
            if (!hasGolden) {
                db.delete(TABLE_BOOKINGS, null, null);
                db.delete(TABLE_HALLS, null, null);
                seedHalls(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_ROLE + " TEXT NOT NULL DEFAULT 'USER', " +
                COL_USER_IMAGE + " TEXT, " +
                COL_USER_DEPARTMENT + " TEXT DEFAULT 'General')");

        db.execSQL("CREATE TABLE " + TABLE_HALLS + " (" +
                COL_HALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HALL_NAME + " TEXT NOT NULL, " +
                COL_HALL_LOCATION + " TEXT, " +
                COL_HALL_CAPACITY + " INTEGER NOT NULL, " +
                COL_HALL_AMENITIES + " TEXT, " +
                COL_HALL_PRICE + " REAL DEFAULT 0, " +
                COL_HALL_DESCRIPTION + " TEXT, " +
                COL_HALL_IMAGE_URIS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_HALL_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_EVENT_NAME + " TEXT NOT NULL, " +
                COL_BOOKING_DEPARTMENT + " TEXT NOT NULL, " +
                COL_BOOKING_DATE + " TEXT NOT NULL, " +
                COL_BOOKING_SLOT + " TEXT NOT NULL, " +
                COL_BOOKING_ATTENDEES + " INTEGER NOT NULL, " +
                COL_BOOKING_STATUS + " TEXT NOT NULL DEFAULT 'PENDING', " +
                COL_BOOKING_REMARKS + " TEXT, " +
                COL_BOOKING_RECEIPT_NO + " TEXT, " +
                COL_BOOKING_CREATED_AT + " TEXT, " +
                COL_BOOKING_UPDATED_AT + " TEXT, " +
                "FOREIGN KEY(" + COL_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_BOOKING_HALL_ID + ") REFERENCES " + TABLE_HALLS + "(" + COL_HALL_ID + "))");

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // RCSS Hall Administrator account
        ContentValues manager = new ContentValues();
        manager.put(COL_USER_NAME, "RCSS Hall Administrator");
        manager.put(COL_USER_EMAIL, "admin@rajagiri.edu");
        manager.put(COL_USER_PHONE, "0484-2911111");
        manager.put(COL_USER_PASSWORD, "Admin@123");
        manager.put(COL_USER_ROLE, "MANAGER");
        db.insert(TABLE_USERS, null, manager);

        // Sample Rajagiri Student account
        ContentValues demoUser = new ContentValues();
        demoUser.put(COL_USER_NAME, "Rajagiri Student");
        demoUser.put(COL_USER_EMAIL, "student@rajagiri.edu");
        demoUser.put(COL_USER_PHONE, "9876543210");
        demoUser.put(COL_USER_PASSWORD, "Student@123");
        demoUser.put(COL_USER_ROLE, "USER");
        db.insert(TABLE_USERS, null, demoUser);

        seedHalls(db);
    }

    private void seedHalls(SQLiteDatabase db) {
        // 1. Golden Auerole
        insertHall(db, "Golden Auerole", "Main Academic Block, Kalamassery Campus", 600,
                "Central AC,Surround Sound,Stage Projector,Stage Lighting,VIP Green Room,Podium Mic", 0,
                "The premier central auditorium of Rajagiri College of Social Sciences, host to convocations, national conferences, academic symposiums and college fests.",
                "res:img_auditorium_1,res:img_auditorium_2,res:img_auditorium_3");

        // 2. Carmel Hall
        insertHall(db, "Carmel Hall", "Carmel Block, First Floor", 250,
                "AC,HD Laser Projection,Acoustic Sound System,Campus WiFi,Smart Podium,Lavalier Mics", 0,
                "Spacious multipurpose hall ideal for departmental symposiums, guest lectures, academic workshops and institutional ceremonies.",
                "res:img_seminar_1,res:img_seminar_2,res:img_seminar_3");

        // 3. Chavera Hall
        insertHall(db, "Chavera Hall", "Chavara Academic Complex, Second Floor", 200,
                "AC,Dual Laser Projectors,Surround Audio,Smart Podium,High-speed WiFi", 0,
                "Named in honor of St. Kuriakose Elias Chavara, this modern tiered auditorium is tailored for research presentations, cultural club meets and interactive sessions.",
                "res:img_miniaud_1,res:img_miniaud_2,res:img_miniaud_3");

        // 4. Assumption Hall
        insertHall(db, "Assumption Hall", "Management Block, Ground Floor", 120,
                "AC,Smart Video Conferencing,Interactive Display,Executive Seating,WiFi", 0,
                "An executive seminar and conference hall for panel discussions, faculty development programs, placement drives and committee meetings.",
                "res:img_conference_1,res:img_conference_2,res:img_conference_3");

        // 5. Esperansa Hall
        insertHall(db, "Esperansa Hall", "Student Amenities Block, Third Floor", 150,
                "AC,Interactive Smart Screen,Sound System,Flexible Seating,High-speed WiFi", 0,
                "Vibrant collaborative hall dedicated to student clubs, creative workshops, forum discussions, training sessions and social work initiatives.",
                "res:img_smartclass_1,res:img_smartclass_2,res:img_smartclass_3");
    }

    private void insertHall(SQLiteDatabase db, String name, String location, int capacity,
                             String amenities, double price, String description, String imageUris) {
        ContentValues cv = new ContentValues();
        cv.put(COL_HALL_NAME, name);
        cv.put(COL_HALL_LOCATION, location);
        cv.put(COL_HALL_CAPACITY, capacity);
        cv.put(COL_HALL_AMENITIES, amenities);
        cv.put(COL_HALL_PRICE, price);
        cv.put(COL_HALL_DESCRIPTION, description);
        cv.put(COL_HALL_IMAGE_URIS, imageUris);
        db.insert(TABLE_HALLS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            ensureUserProfileColumns(db);
            ensureDepartmentColumn(db);
        }
    }
}
