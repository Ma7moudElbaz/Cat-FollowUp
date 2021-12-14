package com.example.followup.requests.adapters;

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
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.models.Purchase_item;

import java.util.ArrayList;
import java.util.List;


public class Purchase_adapter extends RecyclerView.Adapter<Purchase_adapter.ViewHolder> {

    private final List<Purchase_item> items;

    private final Context mContext;

    public Purchase_adapter(Context context, ArrayList<Purchase_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Purchase_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Purchase_adapter.ViewHolder holder, final int position) {

        holder.item_name.setText(items.get(position).getItem_name());
        holder.created_by.setText(items.get(position).getCreated_by_name());
        holder.status.setText(String.valueOf(items.get(position).getStatus_message()));
        holder.color.setText(items.get(position).getColor());
        holder.material.setText(items.get(position).getMaterial());
        holder.quantity.setText(String.valueOf(items.get(position).getQuantity()));

        holder.parent_layout.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, RequestDetailsActivity.class)));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView item_name, created_by, status, color, material, quantity;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            created_by = itemView.findViewById(R.id.created_by);
            status = itemView.findViewById(R.id.status);
            color = itemView.findViewById(R.id.color);
            material = itemView.findViewById(R.id.material);
            quantity = itemView.findViewById(R.id.quantity);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}