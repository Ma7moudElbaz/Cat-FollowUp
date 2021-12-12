package com.example.followup.requests.photography;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.home.projects.Project_item;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.RequestsActivity;

import java.util.ArrayList;
import java.util.List;


public class Photography_adapter extends RecyclerView.Adapter<Photography_adapter.ViewHolder> {

    private final List<Photography_item> items;

    private final Context mContext;

    public Photography_adapter(Context context, ArrayList<Photography_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Photography_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_photography, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Photography_adapter.ViewHolder holder, final int position) {

        holder.item_name.setText(items.get(position).getItem_name());
        holder.created_by.setText(items.get(position).getCreated_by_name());
        holder.status.setText(String.valueOf(items.get(position).getStatus_message()));
        holder.location.setText(items.get(position).getLocation());
        holder.country.setText(items.get(position).getCountry());
        holder.days.setText(items.get(position).getDays());

        holder.parent_layout.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, RequestDetailsActivity.class)));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView item_name, created_by, status, location, country, days;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.client_company);
            created_by = itemView.findViewById(R.id.project_name);
            status = itemView.findViewById(R.id.client_name);
            location = itemView.findViewById(R.id.country);
            country = itemView.findViewById(R.id.timeline);
            days = itemView.findViewById(R.id.status);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}