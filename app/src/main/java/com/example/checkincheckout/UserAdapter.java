package com.example.checkincheckout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> list;

    public UserAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.user_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.email.setText(model.getEmail());
        holder.phone.setText(model.getPhone());
        holder.pass.setText(model.getPassword());

        // ✅ Button click here
       // holder.logout.setOnClickListener(v -> {

            //Toast.makeText(context, "Admin Logged out successfully", Toast.LENGTH_SHORT).show();

           // Intent intent = new Intent(context, DashBoardActivity.class);
           // context.startActivity(intent);});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email,phone,pass;
        //Button logout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.UserName);
            email = itemView.findViewById(R.id.Emailid);
            phone=itemView.findViewById(R.id.Phone);
            pass=itemView.findViewById(R.id.Password);
            //logout = itemView.findViewById(R.id.Logout); // ✅ correct place
        }
    }
}