package com.example.unicareapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName;
    private SharedPreferences sharedPreferences;
    private Button btnUpdateProfile, btnApply, btnCancel, btnUploadPicture;
    private EditText etOldPassword, etNewPassword;
    private LinearLayout layoutUpdateProfile;
    private ImageView ivProfile;

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private Uri profileImageUri; // URI to store the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvFullName = findViewById(R.id.tvFullName);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        layoutUpdateProfile = findViewById(R.id.layoutUpdateProfile);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnApply = findViewById(R.id.btnApply);
        btnCancel = findViewById(R.id.btnCancel);
        btnUploadPicture = findViewById(R.id.btnUploadPicture);
        ivProfile = findViewById(R.id.ivProfile);

        // Retrieve SharedPreferences
        sharedPreferences = getSharedPreferences("user_accounts", MODE_PRIVATE);

        // Get the current logged-in user's full name
        String fullName = sharedPreferences.getString("currentFullName", "Full Name");
        tvFullName.setText(fullName);

        // Update Profile Button Click
        btnUpdateProfile.setOnClickListener(v -> layoutUpdateProfile.setVisibility(View.VISIBLE));

        // Apply Changes
        btnApply.setOnClickListener(v -> applyChanges());

        // Cancel Button Click
        btnCancel.setOnClickListener(v -> layoutUpdateProfile.setVisibility(View.GONE));

        // Upload Photo Button Click
        btnUploadPicture.setOnClickListener(v -> openImagePicker());
    }

    private void applyChanges() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();

        // Retrieve the current password from SharedPreferences
        String savedPassword = sharedPreferences.getString("currentPassword", "");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Password Change Logic
        if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            if (oldPassword.equals(savedPassword)) { // Check if old password matches
                if (!oldPassword.equals(newPassword)) { // Ensure new password is different
                    // Save the new password under the key "currentPassword"
                    editor.putString("currentPassword", newPassword);

                    // Update the password in the user's JSON object
                    String currentUser = sharedPreferences.getString("currentUser", "");
                    if (!currentUser.isEmpty()) {
                        String userJson = sharedPreferences.getString(currentUser, null);
                        if (userJson != null) {
                            try {
                                JSONObject userObject = new JSONObject(userJson);
                                userObject.put("password", newPassword);
                                editor.putString(currentUser, userObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    editor.apply();
                    Toast.makeText(this, "Password changed successfully. Please log in again.", Toast.LENGTH_SHORT).show();
                    logout(); // Log out the user
                } else {
                    Toast.makeText(this, "New password cannot be the same as the old password.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Old password is incorrect.", Toast.LENGTH_SHORT).show();
            }
        } else {
            editor.apply();
            layoutUpdateProfile.setVisibility(View.GONE);
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Open the device's gallery to select an image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.getData(); // Get the selected image URI
            ivProfile.setImageURI(profileImageUri); // Set the image to the ImageView
            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
        }
    }
}