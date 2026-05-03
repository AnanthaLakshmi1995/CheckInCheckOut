package com.example.checkincheckout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    Context context;
    ArrayList<AttendanceModel> list;
    public AttendanceAdapter(Context context, ArrayList<AttendanceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.attendance_list_items, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceModel model = list.get(position);

        holder.name.setText("Username: " + model.getUsername());
        holder.date.setText("Date:"+ model.getDate());
        holder.checkIn.setText("Check-in: " + model.getCheckIn());
        if(model.getCheckOut() != null) {
            holder.checkOut.setText("Check-out: " + model.getCheckOut());
        } else {
            holder.checkOut.setText("Check-out: Not yet");
        }
        holder.working_hours.setText("Working_hours: " +model.getWorkingHours());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, checkIn, checkOut,date,working_hours;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.UserName);
            date=itemView.findViewById(R.id.Date);
            checkIn = itemView.findViewById(R.id.CheckIn);
            checkOut = itemView.findViewById(R.id.CheckOut);
            working_hours= itemView.findViewById(R.id.working_hours);

        }
    }
}
