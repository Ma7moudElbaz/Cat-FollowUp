package com.example.followup.home.projects;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;


public class Projects_adapter_with_callback extends RecyclerView.Adapter<Projects_adapter_with_callback.ViewHolder> {

    private final List<Project_item> items;

    private final Context mContext;
    private AdapterCallback mAdapterCallback;

    public Projects_adapter_with_callback(Context context, Fragment fragment, ArrayList<Project_item> items) {
        try {
            this.mAdapterCallback = ((AdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Projects_adapter_with_callback.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_projects, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Projects_adapter_with_callback.ViewHolder holder, final int position) {

        if (UserType.canEditProject(mContext, items.get(position).getCreated_by_id(), items.get(position).getAssigned_to())
                && items.get(position).getStatus_code() == 1) {
            holder.cancel.setVisibility(View.VISIBLE);
            holder.done.setVisibility(View.VISIBLE);
        } else {
            holder.cancel.setVisibility(View.GONE);
            holder.done.setVisibility(View.GONE);
        }

        holder.client_company.setText(items.get(position).getClient_company());
        holder.project_name.setText(items.get(position).getProject_name());
        holder.client_name.setText(items.get(position).getClient_name());
        holder.country.setText(items.get(position).getProject_country());
        holder.timeline.setText(items.get(position).getProject_timeline());
        holder.status.setText(String.valueOf(items.get(position).getStatus_message()));
        holder.created_by.setText(items.get(position).getCreated_by());

        holder.view_requests.setOnClickListener(v -> {
            Intent i = new Intent(mContext, RequestsActivity.class);
            i.putExtra("project_id", items.get(position).getId());
            i.putExtra("project_name", items.get(position).getProject_name());
            i.putExtra("can_edit_project", UserType.canEditProject(mContext, items.get(position).getCreated_by_id(), items.get(position).getAssigned_to()));
            mContext.startActivity(i);
        });

        holder.done.setOnClickListener(v -> mAdapterCallback.adapterCallback("done", items.get(position).getId()));
        holder.cancel.setOnClickListener(v -> mAdapterCallback.adapterCallback("cancel", items.get(position).getId()));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView client_company, project_name, client_name, country, timeline, status, created_by;
        final AppCompatButton done, cancel, view_requests;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            client_company = itemView.findViewById(R.id.client_company);
            project_name = itemView.findViewById(R.id.project_name);
            client_name = itemView.findViewById(R.id.client_name);
            country = itemView.findViewById(R.id.country);
            timeline = itemView.findViewById(R.id.timeline);
            status = itemView.findViewById(R.id.status);
            created_by = itemView.findViewById(R.id.created_by);

            done = itemView.findViewById(R.id.done);
            cancel = itemView.findViewById(R.id.cancel);
            view_requests = itemView.findViewById(R.id.view_requests);
        }
    }

    public interface AdapterCallback {
        void adapterCallback(String action, int project_id);
    }
}