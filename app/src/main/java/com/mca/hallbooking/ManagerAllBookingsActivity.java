package com.mca.hallbooking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.mca.hallbooking.adapter.RequestAdapter;
import com.mca.hallbooking.dao.BookingDAO;
import com.mca.hallbooking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class ManagerAllBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerAllBookings;
    private BookingDAO bookingDAO;
    private TextView filterAll, filterPending, filterApproved, filterRejected;
    private TextView tvTotalStats, tvEmptyMessage;
    private View layoutEmptyAllBookings;
    private TextInputEditText etSearchBookings;

    private List<Booking> currentCategoryBookings = new ArrayList<>();
    private String selectedStatus = null; // null = All

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_all_bookings);

        bookingDAO = new BookingDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.menu_all_bookings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerAllBookings = findViewById(R.id.recyclerAllBookings);
        recyclerAllBookings.setLayoutManager(new LinearLayoutManager(this));

        filterAll = findViewById(R.id.filterAll);
        filterPending = findViewById(R.id.filterPending);
        filterApproved = findViewById(R.id.filterApproved);
        filterRejected = findViewById(R.id.filterRejected);

        tvTotalStats = findViewById(R.id.tvTotalStats);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        layoutEmptyAllBookings = findViewById(R.id.layoutEmptyAllBookings);
        etSearchBookings = findViewById(R.id.etSearchBookings);

        filterAll.setOnClickListener(v -> {
            selectedStatus = null;
            highlight(filterAll);
            loadCategoryData();
        });

        filterPending.setOnClickListener(v -> {
            selectedStatus = Booking.STATUS_PENDING;
            highlight(filterPending);
            loadCategoryData();
        });

        filterApproved.setOnClickListener(v -> {
            selectedStatus = Booking.STATUS_APPROVED;
            highlight(filterApproved);
            loadCategoryData();
        });

        filterRejected.setOnClickListener(v -> {
            selectedStatus = Booking.STATUS_REJECTED;
            highlight(filterRejected);
            loadCategoryData();
        });

        if (etSearchBookings != null) {
            etSearchBookings.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterSearch(s != null ? s.toString() : "");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        highlight(filterAll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategoryData();
    }

    private void highlight(TextView selected) {
        TextView[] all = {filterAll, filterPending, filterApproved, filterRejected};
        for (TextView tv : all) {
            boolean isSelected = (tv == selected);
            tv.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
            tv.setTextColor(getResources().getColor(isSelected ? R.color.white : R.color.primary));
        }
    }

    private void loadCategoryData() {
        if (selectedStatus == null) {
            currentCategoryBookings = bookingDAO.getAllBookings();
        } else {
            currentCategoryBookings = bookingDAO.getBookingsByStatus(selectedStatus);
        }

        if (tvTotalStats != null) {
            int total = bookingDAO.getAllBookings().size();
            tvTotalStats.setText(total + " Total Bookings");
        }

        if (etSearchBookings != null && etSearchBookings.getText() != null && !etSearchBookings.getText().toString().isEmpty()) {
            filterSearch(etSearchBookings.getText().toString());
        } else {
            renderList(currentCategoryBookings);
        }
    }

    private void filterSearch(String query) {
        if (currentCategoryBookings == null) return;
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) {
            renderList(currentCategoryBookings);
            return;
        }

        List<Booking> filtered = new ArrayList<>();
        for (Booking b : currentCategoryBookings) {
            if ((b.getEventName() != null && b.getEventName().toLowerCase().contains(q)) ||
                (b.getUserName() != null && b.getUserName().toLowerCase().contains(q)) ||
                (b.getDepartment() != null && b.getDepartment().toLowerCase().contains(q)) ||
                (b.getHallName() != null && b.getHallName().toLowerCase().contains(q)) ||
                (b.getBookingDate() != null && b.getBookingDate().toLowerCase().contains(q)) ||
                (b.getReceiptNo() != null && b.getReceiptNo().toLowerCase().contains(q))) {
                filtered.add(b);
            }
        }
        renderList(filtered);
    }

    private void renderList(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            recyclerAllBookings.setVisibility(View.GONE);
            if (layoutEmptyAllBookings != null) layoutEmptyAllBookings.setVisibility(View.VISIBLE);
        } else {
            recyclerAllBookings.setVisibility(View.VISIBLE);
            if (layoutEmptyAllBookings != null) layoutEmptyAllBookings.setVisibility(View.GONE);

            RequestAdapter adapter = new RequestAdapter(bookings, null, false);
            recyclerAllBookings.setAdapter(adapter);
        }
    }
}
