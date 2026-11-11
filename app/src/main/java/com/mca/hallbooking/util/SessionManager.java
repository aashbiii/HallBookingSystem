package com.mca.hallbooking.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mca.hallbooking.model.User;

/** Lightweight session store backed by SharedPreferences. */
public class SessionManager {

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_PROFILE_IMAGE = "profile_image";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(User user) {
        prefs.edit()
                .putLong(KEY_USER_ID, user.getId())
                .putString(KEY_NAME, user.getName())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_ROLE, user.getRole())
                .putString(KEY_PHONE, user.getPhone() != null ? user.getPhone() : "")
                .putString(KEY_DEPARTMENT, user.getDepartment() != null ? user.getDepartment() : "General")
                .putString(KEY_PROFILE_IMAGE, user.getProfileImage() != null ? user.getProfileImage() : "")
                .apply();
    }

    public void updateSession(String name, String phone, String department, String profileImage) {
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null) editor.putString(KEY_NAME, name);
        if (phone != null) editor.putString(KEY_PHONE, phone);
        if (department != null) editor.putString(KEY_DEPARTMENT, department);
        if (profileImage != null) editor.putString(KEY_PROFILE_IMAGE, profileImage);
        editor.apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getLong(KEY_USER_ID, -1) != -1;
    }

    public boolean isManager() {
        return User.ROLE_MANAGER.equals(getRole());
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, User.ROLE_USER);
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public String getDepartment() {
        return prefs.getString(KEY_DEPARTMENT, "General");
    }

    public String getProfileImage() {
        return prefs.getString(KEY_PROFILE_IMAGE, "");
    }
}
