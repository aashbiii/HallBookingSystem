package com.mca.hallbooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mca.hallbooking.dao.UserDAO;
import com.mca.hallbooking.model.User;
import com.mca.hallbooking.util.Constants;
import com.mca.hallbooking.util.ImageUtil;
import com.mca.hallbooking.util.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileAvatar;
    private FrameLayout frameAvatarPicker;
    private TextView tvHeaderName, tvHeaderRole, tvHeaderEmail;
    private TextInputEditText etProfileName, etProfileEmail, etProfilePhone;
    private TextInputLayout layoutDeptDropdown;
    private AutoCompleteTextView actvProfileDepartment;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSaveProfile, btnLogout;

    private UserDAO userDAO;
    private SessionManager sessionManager;
    private User currentUser;
    private String selectedProfileImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    onImagePicked(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadUserData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        frameAvatarPicker = findViewById(R.id.frameAvatarPicker);
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderRole = findViewById(R.id.tvHeaderRole);
        tvHeaderEmail = findViewById(R.id.tvHeaderEmail);

        etProfileName = findViewById(R.id.etProfileName);
        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfilePhone = findViewById(R.id.etProfilePhone);
        layoutDeptDropdown = findViewById(R.id.layoutDeptDropdown);
        actvProfileDepartment = findViewById(R.id.actvProfileDepartment);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Setup Department dropdown
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, Constants.RCSS_DEPARTMENTS);
        actvProfileDepartment.setAdapter(deptAdapter);

        // Click on Avatar to pick photo
        if (frameAvatarPicker != null) {
            frameAvatarPicker.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void loadUserData() {
        long userId = sessionManager.getUserId();
        currentUser = userDAO.getUserById(userId);

        if (currentUser == null) {
            currentUser = userDAO.getUserByEmail(sessionManager.getEmail());
        }

        if (currentUser == null) {
            Toast.makeText(this, "Unable to load user profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvHeaderName.setText(currentUser.getName());
        etProfileName.setText(currentUser.getName());

        tvHeaderEmail.setText(currentUser.getEmail());
        etProfileEmail.setText(currentUser.getEmail());

        etProfilePhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");

        boolean isManager = currentUser.isManager();
        if (isManager) {
            tvHeaderRole.setText("RCSS ADMINISTRATOR");
            tvHeaderRole.setBackgroundResource(R.drawable.bg_chip);
            tvHeaderRole.setBackgroundTintList(getColorStateList(R.color.accent));
            tvHeaderRole.setTextColor(getColor(R.color.black));
        } else {
            tvHeaderRole.setText("RCSS STUDENT");
            tvHeaderRole.setBackgroundResource(R.drawable.bg_chip);
            tvHeaderRole.setBackgroundTintList(getColorStateList(R.color.primary_light));
            tvHeaderRole.setTextColor(getColor(R.color.white));
        }

        // Set department
        String dept = currentUser.getDepartment();
        if (dept == null || dept.trim().isEmpty() || "General".equalsIgnoreCase(dept)) {
            dept = isManager ? "General / Administrative Cell" : Constants.RCSS_DEPARTMENTS[0];
        }
        actvProfileDepartment.setText(dept, false);

        // Load Avatar
        selectedProfileImageUri = currentUser.getProfileImage();
        ImageUtil.loadAvatar(this, selectedProfileImageUri, ivProfileAvatar, R.drawable.ic_person);
    }

    private void onImagePicked(Uri sourceUri) {
        String savedPath = ImageUtil.saveProfileImage(this, currentUser.getId(), sourceUri);
        if (savedPath != null) {
            selectedProfileImageUri = savedPath;
            ImageUtil.loadAvatar(this, selectedProfileImageUri, ivProfileAvatar, R.drawable.ic_person);
            Toast.makeText(this, "Profile picture selected. Tap 'Save Profile Changes' to apply.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to load selected image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        String name = etProfileName.getText() != null ? etProfileName.getText().toString().trim() : "";
        String phone = etProfilePhone.getText() != null ? etProfilePhone.getText().toString().trim() : "";
        String dept = actvProfileDepartment.getText() != null ? actvProfileDepartment.getText().toString().trim() : "General";

        if (TextUtils.isEmpty(name)) {
            etProfileName.setError("Name is required");
            etProfileName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etProfilePhone.setError("Phone number is required");
            etProfilePhone.requestFocus();
            return;
        }

        // Password change handling if entered
        String currentPass = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        boolean changingPassword = !TextUtils.isEmpty(currentPass) || !TextUtils.isEmpty(newPass) || !TextUtils.isEmpty(confirmPass);

        if (changingPassword) {
            if (TextUtils.isEmpty(currentPass)) {
                etCurrentPassword.setError("Please enter your current password");
                etCurrentPassword.requestFocus();
                return;
            }
            if (!currentPass.equals(currentUser.getPassword())) {
                etCurrentPassword.setError("Current password does not match");
                etCurrentPassword.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
                etNewPassword.setError("New password must be at least 6 characters");
                etNewPassword.requestFocus();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            userDAO.updatePassword(currentUser.getId(), newPass);
            currentUser.setPassword(newPass);
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        }

        // Save profile
        boolean updated = userDAO.updateProfile(currentUser.getId(), name, phone, dept, selectedProfileImageUri);
        if (updated) {
            currentUser.setName(name);
            currentUser.setPhone(phone);
            currentUser.setDepartment(dept);
            currentUser.setProfileImage(selectedProfileImageUri);

            sessionManager.updateSession(name, phone, dept, selectedProfileImageUri);

            tvHeaderName.setText(name);
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out of your account?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
