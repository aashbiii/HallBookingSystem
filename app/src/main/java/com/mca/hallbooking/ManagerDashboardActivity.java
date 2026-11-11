package com.mca.hallbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.mca.hallbooking.adapter.RequestAdapter;
import com.mca.hallbooking.dao.BookingDAO;
import com.mca.hallbooking.dao.HallDAO;
import com.mca.hallbooking.model.Booking;
import com.mca.hallbooking.util.ImageUtil;
import com.mca.hallbooking.util.SessionManager;

import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private RecyclerView recyclerRequests;
    private View layoutEmpty;
    private TextView tvPendingCount, tvHallCountBadge, tvTotalBookingsBadge;
    private BookingDAO bookingDAO;
    private HallDAO hallDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        bookingDAO = new BookingDAO(this);
        hallDAO = new HallDAO(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_manage_halls, R.string.nav_manage_halls);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerRequests = findViewById(R.id.recyclerRequests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvHallCountBadge = findViewById(R.id.tvHallCountBadge);
        tvTotalBookingsBadge = findViewById(R.id.tvTotalBookingsBadge);

        // Outside Cards Click Listeners
        View cardManageHalls = findViewById(R.id.cardManageHalls);
        if (cardManageHalls != null) {
            cardManageHalls.setOnClickListener(v -> startActivity(new Intent(this, ManagerHallsActivity.class)));
        }

        View cardAllBookings = findViewById(R.id.cardAllBookings);
        if (cardAllBookings != null) {
            cardAllBookings.setOnClickListener(v -> startActivity(new Intent(this, ManagerAllBookingsActivity.class)));
        }

        // Set manager details in drawer header and wire profile click
        View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            View layoutProfileHeader = headerView.findViewById(R.id.layoutProfileHeader);
            if (layoutProfileHeader != null) {
                layoutProfileHeader.setOnClickListener(v -> {
                    if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(this, ProfileActivity.class));
                });
            }
            updateNavHeader(headerView);
        }
    }

    private void updateNavHeader(View headerView) {
        if (headerView == null) return;
        TextView tvName = headerView.findViewById(R.id.tvNavManagerName);
        TextView tvEmail = headerView.findViewById(R.id.tvNavManagerEmail);
        ImageView ivNavAvatar = headerView.findViewById(R.id.ivNavAvatar);
        if (tvName != null && sessionManager.getName() != null) {
            tvName.setText(sessionManager.getName());
        }
        if (tvEmail != null && sessionManager.getEmail() != null) {
            tvEmail.setText(sessionManager.getEmail());
        }
        if (ivNavAvatar != null) {
            ImageUtil.loadAvatar(this, sessionManager.getProfileImage(), ivNavAvatar, R.drawable.ic_person);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navView != null) {
            navView.setCheckedItem(R.id.nav_pending);
            View headerView = navView.getHeaderView(0);
            if (headerView != null) {
                updateNavHeader(headerView);
            }
        }
        loadDashboardData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_pending) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            loadDashboardData();
        } else if (id == R.id.nav_manage_halls) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ManagerHallsActivity.class));
        } else if (id == R.id.nav_all_bookings) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ManagerAllBookingsActivity.class));
        } else if (id == R.id.nav_profile) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_about) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_logout) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            confirmLogout();
        }
        return true;
    }

    private void loadDashboardData() {
        // Pending requests
        List<Booking> pending = bookingDAO.getBookingsByStatus(Booking.STATUS_PENDING);
        int pendingCount = pending.size();
        if (tvPendingCount != null) {
            tvPendingCount.setText(pendingCount + " Pending");
        }

        if (pending.isEmpty()) {
            recyclerRequests.setVisibility(View.GONE);
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerRequests.setVisibility(View.VISIBLE);
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
            RequestAdapter adapter = new RequestAdapter(pending, new RequestAdapter.OnActionListener() {
                @Override
                public void onApprove(Booking booking) {
                    confirmApprove(booking);
                }

                @Override
                public void onReject(Booking booking) {
                    promptRejectReason(booking);
                }
            }, true);
            recyclerRequests.setAdapter(adapter);
        }

        // Update Quick Access Badges
        int totalHalls = hallDAO.getAllHalls().size();
        if (tvHallCountBadge != null) {
            tvHallCountBadge.setText(totalHalls + " Halls");
        }

        int totalBookings = bookingDAO.getAllBookings().size();
        if (tvTotalBookingsBadge != null) {
            tvTotalBookingsBadge.setText(totalBookings + " Total");
        }
    }

    private void confirmApprove(Booking booking) {
        String deptInfo = booking.getDepartment() != null ? " (" + booking.getDepartment() + ")" : "";
        new AlertDialog.Builder(this)
                .setTitle("Approve Booking Request")
                .setMessage("Approve \"" + booking.getEventName() + "\"" + deptInfo + " for " + booking.getHallName() + "?\nThe venue will be officially allocated to the applicant.")
                .setPositiveButton("Approve", (dialog, which) -> {
                    boolean success = bookingDAO.approveBooking(booking.getId());
                    Toast.makeText(this, success ? "Booking request approved" : "Failed to approve", Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptRejectReason(Booking booking) {
        final EditText input = new EditText(this);
        input.setHint("Reason (optional)");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(this)
                .setTitle("Reject Booking")
                .setMessage("Reject \"" + booking.getEventName() + "\"?")
                .setView(input)
                .setPositiveButton("Reject", (dialog, which) -> {
                    String remarks = input.getText().toString().trim();
                    boolean success = bookingDAO.rejectBooking(booking.getId(), remarks);
                    Toast.makeText(this, success ? "Booking rejected" : "Failed to reject", Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            loadDashboardData();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            confirmLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
