package com.example.unicareapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText etLocation, etTitle, etDescription, etSuggestion;
    private Spinner spServiceType, spPriority;
    private SharedPreferences sharedPreferences;
    private ImageView ivAttachment;
    private Uri selectedImageUri;

    // Launcher for selecting an image
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        selectedImageUri = imageUri; // Store the URI
                        ivAttachment.setImageURI(imageUri); // Display the image
                        ivAttachment.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Initialize views
        etLocation = findViewById(R.id.etLocation);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSuggestion = findViewById(R.id.etSuggestion);
        spServiceType = findViewById(R.id.spServiceType);
        spPriority = findViewById(R.id.spPriority);
        ivAttachment = findViewById(R.id.ivAttachment);
        Button btnAttach = findViewById(R.id.btnAttach);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("RequestData", Context.MODE_PRIVATE);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set click listeners
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        btnAttach.setOnClickListener(v -> openFilePicker());
        btnSubmit.setOnClickListener(v -> submitRequest());
        btnCancel.setOnClickListener(v -> cancelRequest());

        // Clear location field on touch
        etLocation.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etLocation.getRight() - etLocation.getCompoundDrawables()[2].getBounds().width())) {
                    etLocation.setText(""); // Clear the text
                    return true;
                }
            }
            return false;
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            etLocation.setText("Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());
                        } else {
                            Toast.makeText(RequestActivity.this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void submitRequest() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String serviceType = spServiceType.getSelectedItem().toString();
        String issueTitle = etTitle.getText().toString().trim();
        String priorityLevel = spPriority.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();
        String suggestion = etSuggestion.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Validate required fields
        if (issueTitle.isEmpty()) {
            Toast.makeText(this, "Please enter the issue title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter the description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (location.isEmpty()) {
            Toast.makeText(this, "Please enter or fetch your location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a JSON object for the request
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("serviceType", serviceType);
            requestJson.put("issueTitle", issueTitle);
            requestJson.put("priorityLevel", priorityLevel);
            requestJson.put("description", description);
            requestJson.put("suggestion", suggestion);
            requestJson.put("location", location);
            requestJson.put("date", date);
            requestJson.put("status", "Submitted");

            if (selectedImageUri != null) {
                requestJson.put("attachmentUri", selectedImageUri.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Save the request to SharedPreferences
        String requestsJsonString = sharedPreferences.getString("requests", "[]");
        try {
            JSONArray requestsJsonArray = new JSONArray(requestsJsonString);
            requestsJsonArray.put(requestJson);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("requests", requestsJsonArray.toString());
            editor.apply();

            // Log the saved request
            Log.d("RequestActivity", "Request saved: " + requestJson.toString());

            Toast.makeText(this, "Request submitted successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the current activity
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cancelRequest() {
        Toast.makeText(this, "Request canceled", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Please enable location permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}