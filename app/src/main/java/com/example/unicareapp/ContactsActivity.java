package com.example.unicareapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView rvContacts;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data for contacts
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact("Maintenance", "0912435665"));
        contactList.add(new Contact("Dormitory", "0976565432"));
        contactList.add(new Contact("Cafeteria", "0987065423"));
        contactList.add(new Contact("Security", "0976568909"));
        contactList.add(new Contact("StudentsUnion", "0987655398"));
        contactList.add(new Contact("Internet", "0965432356"));
        contactList.add(new Contact("E-Student", "0965432456"));
        contactList.add(new Contact("Healthcare", "0976543423"));
        contactList.add(new Contact("Water Supply", "0914323445"));

        contactAdapter = new ContactAdapter(contactList);
        rvContacts.setAdapter(contactAdapter);
    }
}