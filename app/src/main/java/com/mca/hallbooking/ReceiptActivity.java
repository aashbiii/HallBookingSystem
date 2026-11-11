package com.mca.hallbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mca.hallbooking.dao.BookingDAO;
import com.mca.hallbooking.model.Booking;

public class ReceiptActivity extends AppCompatActivity {

    private Booking booking;
    private BookingDAO bookingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        long bookingId = getIntent().getLongExtra("booking_id", -1);
        bookingDAO = new BookingDAO(this);
        booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            Toast.makeText(this, "Booking details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        bindReceipt();

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void bindReceipt() {
        String slipNo = booking.getReceiptNo() != null ? booking.getReceiptNo() : "RCSS-APP-" + booking.getId();
        ((TextView) findViewById(R.id.tvReceiptNo)).setText("Approval Ref: " + slipNo);

        setRow(R.id.rowUserName, "Applicant / Booked By", booking.getUserName());
        setRow(R.id.rowUserEmail, "Email", booking.getUserEmail());
        setRow(R.id.rowDepartment, "Department", booking.getDepartment() != null ? booking.getDepartment() : "General");
        setRow(R.id.rowEventName, "Event Name", booking.getEventName());
        setRow(R.id.rowHallName, "Hall", booking.getHallName());
        setRow(R.id.rowDate, "Event Date", booking.getBookingDate());
        setRow(R.id.rowSlot, "Time Slot", booking.getSlot());
        setRow(R.id.rowAttendees, "Expected Attendees", String.valueOf(booking.getAttendees()));
        setRow(R.id.rowApprovedOn, "Approved On", booking.getUpdatedAt());
        setRow(R.id.rowAuthority, "Approving Authority", "RCSS Hall Administration (Autonomous)");
    }

    private void setRow(int rowId, String label, String value) {
        View row = findViewById(rowId);
        if (row == null) return;
        ((TextView) row.findViewById(R.id.tvLabel)).setText(label);
        ((TextView) row.findViewById(R.id.tvValue)).setText(value == null ? "-" : value);
    }
}
