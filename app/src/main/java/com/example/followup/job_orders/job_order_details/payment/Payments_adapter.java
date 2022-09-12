package com.example.followup.job_orders.job_order_details.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;

import java.util.ArrayList;
import java.util.List;


public class Payments_adapter extends RecyclerView.Adapter<Payments_adapter.ViewHolder> {

    private final List<Payment_item> items;

    private final Context mContext;

    public Payments_adapter(Context context, ArrayList<Payment_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Payments_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Payments_adapter.ViewHolder holder, final int position) {

        holder.payment.setText(items.get(position).getPercentage());
        holder.date.setText(items.get(position).getCreated_at());


        holder.parent_layout.setOnClickListener(v -> {
            if (!items.get(position).getAttachment().equalsIgnoreCase("null")){
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getAttachment()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView payment, date;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payment = itemView.findViewById(R.id.payment);
            date = itemView.findViewById(R.id.date);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}