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

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById( R.id.tvLogin );

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_accounts", MODE_PRIVATE);

        btnSignUp.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (isUsernameTaken(username)) { // Check if the username is already taken
                Toast.makeText(this, "Username already exists. Please choose a different one.", Toast.LENGTH_SHORT).show();
            } else {
                // Create full name
                String fullName = firstName + " " + lastName;

                // Store user data as JSON in SharedPreferences
                JSONObject userObject = new JSONObject();
                try {
                    userObject.put("fullName", fullName);
                    userObject.put("username", username);
                    userObject.put("password", password);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(username, userObject.toString()); // Store user data under username key
                    editor.apply();

                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to Login Page
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tvLogin.setOnClickListener( v -> {
            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
            startActivity( intent );
            finish();
        } );
    }

    // Function to check if the username is already taken
    private boolean isUsernameTaken(String username) {
        return sharedPreferences.contains(username);
    }
}
