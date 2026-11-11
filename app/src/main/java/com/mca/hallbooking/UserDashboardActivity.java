package com.mca.hallbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.mca.hallbooking.adapter.HallAdapter;
import com.mca.hallbooking.dao.HallDAO;
import com.mca.hallbooking.model.Hall;
import com.mca.hallbooking.util.ImageUtil;
import com.mca.hallbooking.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private HallDAO hallDAO;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private RecyclerView recyclerHalls;
    private TextView tvNoHallsFound;
    private TextInputEditText etSearchHalls;
    private List<Hall> allHalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        sessionManager = new SessionManager(this);
        hallDAO = new HallDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        if (navView != null) {
            navView.setNavigationItemSelectedListener(this);
        }

        if (drawerLayout != null && toolbar != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        // Populate drawer header and wire profile click
        if (navView != null) {
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

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) {
            String name = sessionManager.getName();
            tvWelcome.setText(name != null ? "Welcome back, " + name : "Welcome back");
        }

        MaterialButton btnQuickMyBookings = findViewById(R.id.btnQuickMyBookings);
        if (btnQuickMyBookings != null) {
            btnQuickMyBookings.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));
        }

        recyclerHalls = findViewById(R.id.recyclerHalls);
        recyclerHalls.setLayoutManager(new LinearLayoutManager(this));
        tvNoHallsFound = findViewById(R.id.tvNoHallsFound);
        etSearchHalls = findViewById(R.id.etSearchHalls);

        if (etSearchHalls != null) {
            etSearchHalls.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterHalls(s != null ? s.toString() : "");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void updateNavHeader(View headerView) {
        if (headerView == null) return;
        TextView tvNavName = headerView.findViewById(R.id.tvNavUserName);
        TextView tvNavEmail = headerView.findViewById(R.id.tvNavUserEmail);
        ImageView ivNavAvatar = headerView.findViewById(R.id.ivNavAvatar);
        if (tvNavName != null && sessionManager.getName() != null) {
            tvNavName.setText(sessionManager.getName());
        }
        if (tvNavEmail != null && sessionManager.getEmail() != null) {
            tvNavEmail.setText(sessionManager.getEmail());
        }
        if (ivNavAvatar != null) {
            ImageUtil.loadAvatar(this, sessionManager.getProfileImage(), ivNavAvatar, R.drawable.ic_person);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHalls();
        if (navView != null) {
            navView.setCheckedItem(R.id.nav_explore_halls);
            View headerView = navView.getHeaderView(0);
            if (headerView != null) {
                updateNavHeader(headerView);
            }
        }
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) {
            String name = sessionManager.getName();
            tvWelcome.setText(name != null ? "Welcome back, " + name : "Welcome back");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_explore_halls) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_my_bookings) {
            if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, MyBookingsActivity.class));
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

    private void loadHalls() {
        allHalls = hallDAO.getAllHalls();
        if (etSearchHalls != null && etSearchHalls.getText() != null && !etSearchHalls.getText().toString().isEmpty()) {
            filterHalls(etSearchHalls.getText().toString());
        } else {
            renderHalls(allHalls, true);
        }
    }

    private void filterHalls(String query) {
        if (allHalls == null) return;
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) {
            renderHalls(allHalls, false);
            return;
        }

        List<Hall> filtered = new ArrayList<>();
        for (Hall h : allHalls) {
            if ((h.getName() != null && h.getName().toLowerCase().contains(q)) ||
                (h.getLocation() != null && h.getLocation().toLowerCase().contains(q)) ||
                (h.getAmenities() != null && h.getAmenities().toLowerCase().contains(q))) {
                filtered.add(h);
            }
        }
        renderHalls(filtered, false);
    }

    private void renderHalls(List<Hall> halls, boolean animate) {
        if (halls.isEmpty()) {
            recyclerHalls.setVisibility(View.GONE);
            if (tvNoHallsFound != null) tvNoHallsFound.setVisibility(View.VISIBLE);
        } else {
            recyclerHalls.setVisibility(View.VISIBLE);
            if (tvNoHallsFound != null) tvNoHallsFound.setVisibility(View.GONE);

            HallAdapter adapter = new HallAdapter(halls, hall -> {
                Intent intent = new Intent(UserDashboardActivity.this, HallDetailActivity.class);
                intent.putExtra("hall_id", hall.getId());
                startActivity(intent);
            });
            recyclerHalls.setAdapter(adapter);

            if (animate) {
                try {
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
                    recyclerHalls.setLayoutAnimation(animation);
                    recyclerHalls.scheduleLayoutAnimation();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            confirmLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
}
