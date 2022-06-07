package com.example.followup.job_orders.job_order_requests;

import android.content.Context;
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
        if (!items.get(position).isChecked() && holder.checkBox.isChecked()){
            holder.checkBox.setChecked(false);
        }
        if (items.get(position).isChecked()) {
            holder.final_cost_container.setVisibility(View.VISIBLE);
        } else {
            holder.final_cost_container.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            items.get(position).setChecked(isChecked);
            notifyDataSetChanged();
        });

    }

    public List<Job_order_request_item> getSelectedData() {
        List<Job_order_request_item> selectedList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isChecked()) {
//                items.get(i).setFinal_cost();
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
        final TextView request_id,quantity,cost_type;
        final EditText final_cost;
        final LinearLayout final_cost_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            request_id = itemView.findViewById(R.id.request_id);
            quantity = itemView.findViewById(R.id.quantity);
            final_cost = itemView.findViewById(R.id.final_cost);
            cost_type = itemView.findViewById(R.id.cost_type);
            final_cost_container = itemView.findViewById(R.id.final_cost_container);
        }
    }
}