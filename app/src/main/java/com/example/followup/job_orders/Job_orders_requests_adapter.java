package com.example.followup.job_orders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;

import java.util.ArrayList;
import java.util.List;


public class Job_orders_requests_adapter extends RecyclerView.Adapter<Job_orders_requests_adapter.ViewHolder> {

    private final List<Job_order_request_item> items;

    private final Context mContext;

    public Job_orders_requests_adapter(Context context, ArrayList<Job_order_request_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Job_orders_requests_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_order_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Job_orders_requests_adapter.ViewHolder holder, final int position) {
        holder.request_id.setText(items.get(position).getRequest_id());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final CheckBox checkBox;
        final TextView request_id;
        final EditText final_cost;
        final LinearLayout final_cost_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            request_id = itemView.findViewById(R.id.request_id);
            final_cost = itemView.findViewById(R.id.final_cost);
            final_cost_container = itemView.findViewById(R.id.final_cost_container);
        }
    }
}