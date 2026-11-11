package com.mca.hallbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mca.hallbooking.adapter.BookingAdapter;
import com.mca.hallbooking.dao.BookingDAO;
import com.mca.hallbooking.model.Booking;
import com.mca.hallbooking.util.SessionManager;

import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerBookings;
    private View tvEmpty;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingDAO = new BookingDAO(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerBookings = findViewById(R.id.recyclerBookings);
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        long userId = sessionManager.getUserId();
        String userEmail = sessionManager.getEmail();

        com.mca.hallbooking.dao.UserDAO userDAO = new com.mca.hallbooking.dao.UserDAO(this);
        com.mca.hallbooking.model.User u = null;
        if (userId > 0) {
            u = userDAO.getUserById(userId);
        }
        if (u == null && userEmail != null && !userEmail.isEmpty()) {
            u = userDAO.getUserByEmail(userEmail);
        }
        if (u == null) {
            u = userDAO.getUserByEmail("student@rajagiri.edu");
        }
        if (u != null) {
            userId = u.getId();
            userEmail = u.getEmail();
            sessionManager.login(u);
        }

        List<Booking> bookings = bookingDAO.getBookingsForUser(userId, userEmail);
        if (bookings.isEmpty()) {
            recyclerBookings.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerBookings.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            BookingAdapter adapter = new BookingAdapter(bookings, booking -> {
                Intent intent = new Intent(this, ReceiptActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            });
            recyclerBookings.setAdapter(adapter);
        }
    }
}
