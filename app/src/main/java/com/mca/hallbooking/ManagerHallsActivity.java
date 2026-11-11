package com.mca.hallbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mca.hallbooking.adapter.HallAdapter;
import com.mca.hallbooking.dao.HallDAO;
import com.mca.hallbooking.model.Hall;

import java.util.List;

public class ManagerHallsActivity extends AppCompatActivity {

    private RecyclerView recyclerHalls;
    private TextView tvHallCountInfo, tvEmptyHalls;
    private ExtendedFloatingActionButton fabAddHall;
    private HallDAO hallDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_halls);

        hallDAO = new HallDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Manage Campus Halls");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerHalls = findViewById(R.id.recyclerHalls);
        recyclerHalls.setLayoutManager(new LinearLayoutManager(this));
        tvHallCountInfo = findViewById(R.id.tvHallCountInfo);
        tvEmptyHalls = findViewById(R.id.tvEmptyHalls);
        fabAddHall = findViewById(R.id.fabAddHall);

        if (fabAddHall != null) {
            fabAddHall.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditHallActivity.class);
                intent.putExtra("hall_id", -1L);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHalls();
    }

    private void loadHalls() {
        List<Hall> halls = hallDAO.getAllHalls();
        if (tvHallCountInfo != null) {
            tvHallCountInfo.setText(halls.size() + " active venues available. Tap to edit or manage gallery.");
        }

        if (halls.isEmpty()) {
            recyclerHalls.setVisibility(View.GONE);
            if (tvEmptyHalls != null) tvEmptyHalls.setVisibility(View.VISIBLE);
        } else {
            recyclerHalls.setVisibility(View.VISIBLE);
            if (tvEmptyHalls != null) tvEmptyHalls.setVisibility(View.GONE);

            HallAdapter adapter = new HallAdapter(halls, hall -> {
                Intent intent = new Intent(this, EditHallActivity.class);
                intent.putExtra("hall_id", hall.getId());
                startActivity(intent);
            });
            recyclerHalls.setAdapter(adapter);

            try {
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
                recyclerHalls.setLayoutAnimation(animation);
                recyclerHalls.scheduleLayoutAnimation();
            } catch (Exception ignored) {
            }
        }
    }
}
