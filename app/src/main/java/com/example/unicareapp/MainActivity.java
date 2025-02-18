package com.example.unicareapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnProfile, btnRequest, btnHistory, btnContacts, btnEmergency, btnLogout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        btnProfile = findViewById(R.id.btnProfile);
        btnRequest = findViewById(R.id.btnRequest);
        btnContacts = findViewById(R.id.btnContacts);
        btnEmergency = findViewById(R.id.btnEmergency);
        btnLogout = findViewById(R.id.btnLogout);

        btnProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        btnRequest.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RequestActivity.class)));
        btnContacts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));
        btnEmergency.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EmergencyActivity.class)));

        // Logout button logic
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // Show confirmation dialog before logging out
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("currentUser");
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Handle back button press to show logout confirmation instead of exiting
    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }
}
