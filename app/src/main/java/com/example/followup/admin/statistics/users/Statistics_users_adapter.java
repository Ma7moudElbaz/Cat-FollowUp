package com.example.followup.admin.statistics.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.job_orders.job_order_details.JobOrderDetailsActivity;

import java.util.ArrayList;
import java.util.List;


public class Statistics_users_adapter extends RecyclerView.Adapter<Statistics_users_adapter.ViewHolder> {

    private final List<Statistics_user_item> items;

    private final Context mContext;

    public Statistics_users_adapter(Context context, ArrayList<Statistics_user_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Statistics_users_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_user, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Statistics_users_adapter.ViewHolder holder, final int position) {

        holder.name.setText(items.get(position).getName());
        holder.projects_no.setText(String.valueOf(items.get(position).getProjects_no()));
        holder.requests_no.setText(String.valueOf(items.get(position).getRequests_no()));

        holder.parent_layout.setOnClickListener(v -> {
            Intent i = new Intent(mContext, Statistics_single_user.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("user_id", items.get(position).getId());
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView name, projects_no, requests_no;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            projects_no = itemView.findViewById(R.id.projects_no);
            requests_no = itemView.findViewById(R.id.requests_no);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}