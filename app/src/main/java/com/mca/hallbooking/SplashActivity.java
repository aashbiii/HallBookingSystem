package com.mca.hallbooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.mca.hallbooking.util.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        android.view.View content = findViewById(R.id.layoutSplashContent);
        if (content != null) {
            content.setAlpha(0f);
            content.animate().alpha(1f).setDuration(800).start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DELAY_MS);
    }

    private void navigateNext() {
        SessionManager session = new SessionManager(this);
        Intent intent;
        if (session.isLoggedIn()) {
            intent = session.isManager()
                    ? new Intent(this, ManagerDashboardActivity.class)
                    : new Intent(this, UserDashboardActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
