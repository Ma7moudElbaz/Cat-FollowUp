package com.example.followup.home.projects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;

import java.util.ArrayList;
import java.util.List;


public class Companies_adapter_with_callback extends RecyclerView.Adapter<Companies_adapter_with_callback.ViewHolder> {

    private final List<Company_item> items;

    private final AdapterCallback mAdapterCallback;

    public Companies_adapter_with_callback(Fragment fragment, ArrayList<Company_item> items) {
        try {
            this.mAdapterCallback = ((AdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.items = items;
    }

    @NonNull
    @Override
    public Companies_adapter_with_callback.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Companies_adapter_with_callback.ViewHolder holder, final int position) {
        holder.company_name.setText(items.get(position).getName());

        holder.parent_layout.setOnClickListener(v -> mAdapterCallback.adapterCallback(
                items.get(position).getName(), items.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView company_name;
        final RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            company_name = itemView.findViewById(R.id.company_name);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public interface AdapterCallback {
        void adapterCallback(String companyName, String companyId);
    }
}