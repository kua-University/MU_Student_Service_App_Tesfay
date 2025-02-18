package com.example.unicareapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    private RecyclerView rvEmergency;
    private EmergencyAdapter emergencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        rvEmergency = findViewById(R.id.rvEmergency);
        rvEmergency.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data for emergency services
        List<EmergencyService> emergencyServiceList = new ArrayList<>();
        emergencyServiceList.add(new EmergencyService("Ambulance", "0914567663"));
        emergencyServiceList.add(new EmergencyService("Fire", "0914789065"));
        emergencyServiceList.add(new EmergencyService("Security", "0914679804"));

        emergencyAdapter = new EmergencyAdapter(emergencyServiceList);
        rvEmergency.setAdapter(emergencyAdapter);
    }
}