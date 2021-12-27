package com.example.followup.job_orders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.home.projects.Project_item;
import com.example.followup.requests.RequestsActivity;

import java.util.ArrayList;
import java.util.List;


public class Job_orders_adapter extends RecyclerView.Adapter<Job_orders_adapter.ViewHolder> {

    private final List<Job_order_item> items;

    private final Context mContext;

    public Job_orders_adapter(Context context, ArrayList<Job_order_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Job_orders_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Job_orders_adapter.ViewHolder holder, final int position) {

        holder.job_order_number.setText(items.get(position).getName());
        holder.po_number.setText(items.get(position).getPo_number());

        if (items.get(position).getStatus_code() == 7){
            holder.status_color.setBackgroundResource(R.drawable.status_green);
            holder.status.setBackgroundResource(R.drawable.status_green);
            holder.status.setText("Approved");
        }else if (items.get(position).getStatus_code() == 6||items.get(position).getStatus_code() == 9){
            holder.status_color.setBackgroundResource(R.drawable.status_red);
            holder.status.setBackgroundResource(R.drawable.status_red);
            holder.status.setText("Rejected");
        }else{
            holder.status_color.setBackgroundResource(R.drawable.status_yellow);
            holder.status.setBackgroundResource(R.drawable.status_yellow);
            holder.status.setText("In Progress");
        }

        holder.parent_layout.setOnClickListener(v -> {
            Intent i = new Intent(mContext, JobOrderDetailsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("job_order_id",items.get(position).getId());
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView job_order_number, po_number, status;
        final RelativeLayout parent_layout;
        final View status_color;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            job_order_number = itemView.findViewById(R.id.job_order_number);
            po_number = itemView.findViewById(R.id.po_number);
            status = itemView.findViewById(R.id.status);
            status_color = itemView.findViewById(R.id.status_color);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}