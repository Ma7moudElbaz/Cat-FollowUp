package com.example.followup.requests.print;

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
import com.example.followup.requests.photography.Photography_item;

import java.util.ArrayList;
import java.util.List;


public class Print_adapter extends RecyclerView.Adapter<Print_adapter.ViewHolder> {

    private final List<Print_item> items;

    private final Context mContext;

    public Print_adapter(Context context, ArrayList<Print_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Print_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_print, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Print_adapter.ViewHolder holder, final int position) {

        holder.item_name.setText(items.get(position).getItem_name());
        holder.created_by.setText(items.get(position).getCreated_by_name());
        holder.status.setText(String.valueOf(items.get(position).getStatus_message()));
        holder.designer.setText(items.get(position).getDesigner_name());
        holder.print_type.setText(items.get(position).getPrint_type());
        holder.quantity.setText(items.get(position).getQuantity());

        holder.parent_layout.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, RequestDetailsActivity.class)));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView item_name, created_by, status, designer, print_type, quantity;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_name);
            created_by = itemView.findViewById(R.id.created_by);
            status = itemView.findViewById(R.id.status);
            designer = itemView.findViewById(R.id.designer);
            print_type = itemView.findViewById(R.id.print_type);
            quantity = itemView.findViewById(R.id.quantity);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}