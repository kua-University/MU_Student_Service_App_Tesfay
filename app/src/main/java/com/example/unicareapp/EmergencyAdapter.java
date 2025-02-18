package com.example.unicareapp;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder> {

    private List<EmergencyService> emergencyServiceList;

    public EmergencyAdapter(List<EmergencyService> emergencyServiceList) {
        this.emergencyServiceList = emergencyServiceList;
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency, parent, false);
        return new EmergencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position) {
        EmergencyService emergencyService = emergencyServiceList.get(position);
        holder.tvEmergencyName.setText(emergencyService.getName());
        holder.tvEmergencyNumber.setText(emergencyService.getNumber());

        // Set click listener for the Call button
        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyService.getNumber()));
            v.getContext().startActivity(intent);
        });

        // Set click listener for the SMS button
        holder.btnSMS.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + emergencyService.getNumber()));
            intent.putExtra("sms_body", "Emergency! Please help!"); // Pre-filled SMS text
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return emergencyServiceList.size();
    }

    static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmergencyName, tvEmergencyNumber;
        Button btnCall, btnSMS;

        public EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmergencyName = itemView.findViewById(R.id.tvEmergencyName);
            tvEmergencyNumber = itemView.findViewById(R.id.tvEmergencyNumber);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSMS = itemView.findViewById(R.id.btnSMS);
        }
    }
}