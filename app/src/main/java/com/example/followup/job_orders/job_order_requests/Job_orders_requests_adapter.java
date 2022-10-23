package com.example.followup.job_orders.job_order_requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.requests.RequestDetailsActivity;

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
    public void onBindViewHolder(@NonNull Job_orders_requests_adapter.ViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        holder.request_name.setText(items.get(position).getRequest_name());
        holder.quantity.setText(items.get(position).getQuantity());
        holder.final_cost.setText(items.get(position).getFinal_cost());
        String cost_type_text = items.get(position).getCost_type() + " Cost";
        holder.cost_type.setText(cost_type_text);
        holder.final_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                items.get(position).setFinal_cost(holder.final_cost.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!items.get(position).isChecked() && holder.checkBox.isChecked()) {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            items.get(position).setChecked(isChecked);
            if (isChecked) {
                holder.final_cost_container.setVisibility(View.VISIBLE);
            } else {
                holder.final_cost_container.setVisibility(View.GONE);
            }
        });

        holder.request_name.setOnClickListener(view -> {
            Intent i = new Intent(mContext, RequestDetailsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("request_id", items.get(position).getId());
            i.putExtra("type_id", items.get(position).getType_id());
            mContext.startActivity(i);
        });

    }

    public List<Job_order_request_item> getSelectedData() {
        List<Job_order_request_item> selectedList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isChecked()) {
                selectedList.add(items.get(i));
            }
        }
        return selectedList;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final CheckBox checkBox;
        final TextView request_name, quantity, cost_type;
        final EditText final_cost;
        final LinearLayout final_cost_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            request_name = itemView.findViewById(R.id.request_name);
            quantity = itemView.findViewById(R.id.quantity);
            final_cost = itemView.findViewById(R.id.final_cost);
            cost_type = itemView.findViewById(R.id.cost_type);
            final_cost_container = itemView.findViewById(R.id.final_cost_container);
        }
    }
}