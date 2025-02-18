package com.example.unicareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_accounts", MODE_PRIVATE);

        // Login Button Click
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (validateUser(username, password)) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Retrieve full name
                    String fullName = getFullName(username);

                    // Store the current logged-in user details
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentUser", username); // Save current username
                    editor.putString("currentFullName", fullName); // Save current full name
                    editor.putString("currentPassword", password); // Save current password
                    editor.apply();

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close LoginActivity
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign Up Navigation Click
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateUser(String username, String password) {
        String userJson = sharedPreferences.getString(username, null);

        if (userJson != null) {
            try {
                JSONObject userObject = new JSONObject(userJson);
                String storedPassword = userObject.getString("password");

                // Check if the entered password matches the stored password
                return password.equals(storedPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getFullName(String username) {
        String userJson = sharedPreferences.getString(username, null);
        if (userJson != null) {
            try {
                JSONObject userObject = new JSONObject(userJson);
                return userObject.getString("fullName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "Full Name";
    }
}