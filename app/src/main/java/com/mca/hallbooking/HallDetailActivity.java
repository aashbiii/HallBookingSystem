package com.mca.hallbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mca.hallbooking.adapter.ImageCarouselAdapter;
import com.mca.hallbooking.dao.BookingDAO;
import com.mca.hallbooking.dao.HallDAO;
import com.mca.hallbooking.dao.UserDAO;
import com.mca.hallbooking.model.Hall;
import com.mca.hallbooking.model.User;
import com.mca.hallbooking.util.Constants;
import com.mca.hallbooking.util.DateTimeUtils;
import com.mca.hallbooking.util.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class HallDetailActivity extends AppCompatActivity {

    private Hall hall;
    private HallDAO hallDAO;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;

    private TextView tvAvailabilityResult;
    private MaterialButton btnPickDate, btnCheckAvailability, btnBookNow;
    private RadioGroup radioGroupSlots;
    private View layoutBookingForm;
    private TextInputEditText etEventName, etAttendees;
    private AutoCompleteTextView actvDepartment;

    private static final String[] RCSS_DEPARTMENTS = new String[]{
            "Department of Computer Science (MCA / MSc)",
            "Department of Social Work (MSW / BSW)",
            "Department of Commerce & Professional Studies",
            "Department of Management Studies (MBA)",
            "Department of Psychology (MSc / BSc)",
            "Department of Library & Information Science",
            "Department of Personnel Management (MHRM)",
            "College Cultural Committee & Fine Arts",
            "IQAC & College Administration",
            "Placement & Corporate Relations Cell",
            "Student Council & University Club"
    };

    private String selectedDate = null;
    private String selectedSlot = null;
    private boolean slotConfirmedAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_detail);

        sessionManager = new SessionManager(this);
        hallDAO = new HallDAO(this);
        bookingDAO = new BookingDAO(this);

        long hallId = getIntent().getLongExtra("hall_id", -1);
        hall = hallDAO.getHallById(hallId);
        if (hall == null) {
            Toast.makeText(this, "Hall not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hall.getName());
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupImageCarousel();

        bindHallInfo();
        buildSlotOptions();

        btnPickDate = findViewById(R.id.btnPickDate);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnBookNow = findViewById(R.id.btnBookNow);
        tvAvailabilityResult = findViewById(R.id.tvAvailabilityResult);
        layoutBookingForm = findViewById(R.id.layoutBookingForm);
        etEventName = findViewById(R.id.etEventName);
        etAttendees = findViewById(R.id.etAttendees);
        actvDepartment = findViewById(R.id.actvDepartment);

        if (actvDepartment != null) {
            ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, RCSS_DEPARTMENTS);
            actvDepartment.setAdapter(deptAdapter);
            actvDepartment.setText(RCSS_DEPARTMENTS[0], false);
        }

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnCheckAvailability.setOnClickListener(v -> checkAvailability());
        btnBookNow.setOnClickListener(v -> submitBooking());
    }

    private void bindHallInfo() {
        ((TextView) findViewById(R.id.tvHallName)).setText(hall.getName());
        ((TextView) findViewById(R.id.tvLocation)).setText(hall.getLocation());
        ((TextView) findViewById(R.id.tvDescription)).setText(hall.getDescription());
        ((TextView) findViewById(R.id.tvCapacity)).setText(String.valueOf(hall.getCapacity()));
        ((TextView) findViewById(R.id.tvAmenities)).setText(hall.getAmenities().replace(",", "  •  "));
    }

    private void setupImageCarousel() {
        androidx.viewpager2.widget.ViewPager2 pagerImages = findViewById(R.id.pagerImages);
        TextView tvCarouselIndicator = findViewById(R.id.tvCarouselIndicator);

        java.util.List<String> images = new java.util.ArrayList<>();
        if (hall.getImageUris() != null && !hall.getImageUris().trim().isEmpty()) {
            String[] uris = hall.getImageUris().split(",");
            for (String uri : uris) {
                if (!uri.trim().isEmpty()) images.add(uri.trim());
            }
        }
        
        if (images.isEmpty()) {
            images.add(""); // Add one empty string to show default placeholder
        }

        ImageCarouselAdapter adapter = new ImageCarouselAdapter(images);
        if (pagerImages != null) {
            pagerImages.setAdapter(adapter);
            if (tvCarouselIndicator != null) {
                tvCarouselIndicator.setText("1 / " + images.size());
                pagerImages.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        tvCarouselIndicator.setText((position + 1) + " / " + images.size());
                    }
                });
            }
        }
    }

    private void buildSlotOptions() {
        radioGroupSlots = findViewById(R.id.rgSlots);
        for (String slot : Constants.TIME_SLOTS) {
            RadioButton rb = new RadioButton(this);
            rb.setText(slot);
            rb.setTextSize(14);
            rb.setPadding(8, 14, 8, 14);
            rb.setId(View.generateViewId());
            radioGroupSlots.addView(rb);
        }
        radioGroupSlots.setOnCheckedChangeListener((group, checkedId) -> resetAvailabilityState());
    }

    private void showDatePicker() {
        Calendar today = DateTimeUtils.today();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth);
                    selectedDate = DateTimeUtils.format(picked);
                    btnPickDate.setText(selectedDate);
                    resetAvailabilityState();
                },
                today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void resetAvailabilityState() {
        slotConfirmedAvailable = false;
        tvAvailabilityResult.setVisibility(View.GONE);
        layoutBookingForm.setVisibility(View.GONE);
    }

    private void checkAvailability() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        int checkedId = radioGroupSlots.getCheckedRadioButtonId();
        if (checkedId == View.NO_ID) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton checked = findViewById(checkedId);
        selectedSlot = checked.getText().toString();

        boolean taken = bookingDAO.isSlotTaken(hall.getId(), selectedDate, selectedSlot);
        tvAvailabilityResult.setVisibility(View.VISIBLE);

        if (taken) {
            slotConfirmedAvailable = false;
            tvAvailabilityResult.setText("✕ This hall is already booked for the selected date & slot");
            tvAvailabilityResult.setBackgroundResource(R.drawable.bg_status_rejected);
            tvAvailabilityResult.setTextColor(getResources().getColor(R.color.status_rejected));
            layoutBookingForm.setVisibility(View.GONE);
        } else {
            slotConfirmedAvailable = true;
            tvAvailabilityResult.setText("✔ Hall is available for the selected date and slot");
            tvAvailabilityResult.setBackgroundResource(R.drawable.bg_status_approved);
            tvAvailabilityResult.setTextColor(getResources().getColor(R.color.status_approved));
            btnBookNow.setText("Submit Booking Request");
            layoutBookingForm.setVisibility(View.VISIBLE);
        }
    }

    private void submitBooking() {
        if (!slotConfirmedAvailable) {
            Toast.makeText(this, "Please check availability first", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventName = etEventName.getText() != null ? etEventName.getText().toString().trim() : "";
        String attendeesStr = etAttendees.getText() != null ? etAttendees.getText().toString().trim() : "";
        String department = actvDepartment != null && actvDepartment.getText() != null ?
                actvDepartment.getText().toString().trim() : "";

        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(department)) {
            Toast.makeText(this, "Please select your department", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(attendeesStr)) {
            Toast.makeText(this, "Please enter expected attendees", Toast.LENGTH_SHORT).show();
            return;
        }

        int attendees = Integer.parseInt(attendeesStr);
        if (attendees <= 0) {
            Toast.makeText(this, "Attendees must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (attendees > hall.getCapacity()) {
            Toast.makeText(this, "Expected attendees exceed hall capacity (" + hall.getCapacity() + ")", Toast.LENGTH_LONG).show();
            return;
        }

        if (hall == null || hall.getId() <= 0) {
            Toast.makeText(this, "Please select a valid venue", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure valid userId from session or fallback to current student account
        long userId = sessionManager.getUserId();
        UserDAO userDAO = new UserDAO(this);
        com.mca.hallbooking.model.User u = null;
        if (userId > 0) {
            u = userDAO.getUserById(userId);
        }
        if (u == null) {
            String email = sessionManager.getEmail();
            if (!TextUtils.isEmpty(email)) {
                u = userDAO.getUserByEmail(email);
            }
            if (u == null) {
                u = userDAO.getUserByEmail("student@rajagiri.edu");
            }
            if (u != null) {
                userId = u.getId();
                sessionManager.login(u);
            }
        }

        // Re-check right before submitting request to avoid race condition
        if (bookingDAO.isSlotTaken(hall.getId(), selectedDate, selectedSlot)) {
            Toast.makeText(this, "Sorry, this slot was just booked. Please choose another.", Toast.LENGTH_LONG).show();
            resetAvailabilityState();
            return;
        }

        long bookingId = bookingDAO.createBooking(userId, hall.getId(),
                eventName, department, selectedDate, selectedSlot, attendees);

        if (bookingId > 0) {
            Toast.makeText(this, "Booking request submitted! Awaiting administrator approval.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyBookingsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit booking request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
