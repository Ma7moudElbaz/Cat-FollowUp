package com.example.followup.home.notifications;

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
import com.example.followup.job_orders.JobOrderDetailsActivity;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.RequestsActivity;

import java.util.ArrayList;
import java.util.List;


public class Notification_adapter extends RecyclerView.Adapter<Notification_adapter.ViewHolder> {

    private final List<Notification_item> items;

    private final Context mContext;

    public Notification_adapter(Context context, ArrayList<Notification_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.message.setText(items.get(position).getMessage());

        holder.parent_layout.setOnClickListener(v -> {
            Intent i;
            if (items.get(position).getAction_type().equals("request")||items.get(position).getAction_type().equals("cost")) {
                i = new Intent(mContext, RequestDetailsActivity.class);
                i.putExtra("request_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            } else if (items.get(position).getAction_type().equals("jo")) {
                i = new Intent(mContext, JobOrderDetailsActivity.class);
                i.putExtra("job_order_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            }else if (items.get(position).getAction_type().equals("project")){
                i = new Intent(mContext, RequestsActivity.class);
                i.putExtra("project_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            }
//            switch (items.get(position).getRequest_type()) {
//                case "purchasing":
//                    i = new Intent(mContext, PurchasingDetailsActivity.class);
//                    i.putExtra("id", items.get(position).getRequest_id());
//                    mContext.startActivity(i);
//                    break;
//                case "printing":
//                    i = new Intent(mContext, PrintingDetailsActivity.class);
//                    i.putExtra("id", items.get(position).getRequest_id());
//                    mContext.startActivity(i);
//                    break;
//                case "production":
//                    i = new Intent(mContext, ProductionDetailsActivity.class);
//                    i.putExtra("id", items.get(position).getRequest_id());
//                    mContext.startActivity(i);
//                    break;
//            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView message;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}