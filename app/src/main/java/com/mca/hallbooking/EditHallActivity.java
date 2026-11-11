package com.mca.hallbooking;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mca.hallbooking.adapter.EditImageAdapter;
import com.mca.hallbooking.dao.HallDAO;
import com.mca.hallbooking.model.Hall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditHallActivity extends AppCompatActivity {

    private Hall hall;
    private HallDAO hallDAO;

    private TextInputEditText etName, etLocation, etCapacity, etAmenities, etDescription;
    private TextView tvPhotoCount;
    private RecyclerView recyclerImages;
    private EditImageAdapter imageAdapter;
    private List<String> currentImages;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hall);

        long hallId = getIntent().getLongExtra("hall_id", -1);
        hallDAO = new HallDAO(this);

        boolean isNew = (hallId == -1);
        if (isNew) {
            hall = new Hall();
            hall.setName("");
            hall.setLocation("");
            hall.setAmenities("");
            hall.setDescription("");
            hall.setPrice(0.0);
        } else {
            hall = hallDAO.getHallById(hallId);
            if (hall == null) {
                Toast.makeText(this, "Hall not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(isNew ? "Add New Hall" : getString(R.string.edit_hall));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etAmenities = findViewById(R.id.etAmenities);
        etDescription = findViewById(R.id.etDescription);
        tvPhotoCount = findViewById(R.id.tvPhotoCount);
        recyclerImages = findViewById(R.id.recyclerImages);
        
        MaterialButton btnAddImage = findViewById(R.id.btnAddImage);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        bindData();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String localUri = copyUriToInternalStorage(uri);
                        if (localUri != null) {
                            currentImages.add(localUri);
                            imageAdapter.notifyItemInserted(currentImages.size() - 1);
                            updatePhotoCount();
                        } else {
                            Toast.makeText(this, "Failed to import photo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnAddImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void bindData() {
        if (etName != null && hall.getName() != null) etName.setText(hall.getName());
        if (etLocation != null && hall.getLocation() != null) etLocation.setText(hall.getLocation());
        if (etCapacity != null) etCapacity.setText(String.valueOf(hall.getCapacity()));
        if (etAmenities != null && hall.getAmenities() != null) etAmenities.setText(hall.getAmenities());
        if (etDescription != null && hall.getDescription() != null) etDescription.setText(hall.getDescription());

        currentImages = new ArrayList<>();
        if (hall.getImageUris() != null && !hall.getImageUris().trim().isEmpty()) {
            String[] split = hall.getImageUris().split(",");
            for (String s : split) {
                if (!s.trim().isEmpty()) currentImages.add(s.trim());
            }
        }

        imageAdapter = new EditImageAdapter(currentImages, this::updatePhotoCount);
        recyclerImages.setAdapter(imageAdapter);
        updatePhotoCount();
    }

    private void updatePhotoCount() {
        if (tvPhotoCount != null) {
            int count = currentImages != null ? currentImages.size() : 0;
            tvPhotoCount.setText(count + (count == 1 ? " photo" : " photos"));
        }
    }

    private String copyUriToInternalStorage(Uri sourceUri) {
        try {
            File dir = new File(getFilesDir(), "hall_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(dir, "hall_" + hall.getId() + "_" + System.currentTimeMillis() + ".jpg");

            try (InputStream in = getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(destFile)) {
                if (in == null) return null;
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            return Uri.fromFile(destFile).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveChanges() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String capStr = etCapacity.getText() != null ? etCapacity.getText().toString().trim() : "";
        String amenities = etAmenities.getText() != null ? etAmenities.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(capStr)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            hall.setName(name);
            hall.setLocation(location);
            hall.setCapacity(Integer.parseInt(capStr));
            hall.setPrice(0.0);
            hall.setAmenities(amenities);
            hall.setDescription(desc);
            hall.setImageUris(TextUtils.join(",", currentImages));

            if (hall.getId() <= 0) {
                long newId = hallDAO.insertHall(hall);
                if (newId != -1) {
                    Toast.makeText(this, "New hall added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to create hall", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean success = hallDAO.updateHall(hall);
                if (success) {
                    Toast.makeText(this, "Hall updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update hall", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}