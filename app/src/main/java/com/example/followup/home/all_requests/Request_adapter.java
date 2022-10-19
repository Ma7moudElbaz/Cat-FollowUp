package com.example.followup.home.all_requests;

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

import java.util.ArrayList;
import java.util.List;


public class Request_adapter extends RecyclerView.Adapter<Request_adapter.ViewHolder> {

    private final List<Request_item> items;

    private final Context mContext;

    public Request_adapter(Context context, ArrayList<Request_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Request_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Request_adapter.ViewHolder holder, final int position) {

        holder.project_name.setText(items.get(position).getProject_name());
        holder.request_name.setText(items.get(position).getRequest_name());
        holder.request_type.setText(items.get(position).getRequest_type());
        holder.status.setText(String.valueOf(items.get(position).getStatus()));
        holder.created_by.setText(items.get(position).getCreated_by());

        holder.parent_layout.setOnClickListener(v -> {
            Intent i = new Intent(mContext, RequestDetailsActivity.class);
            i.putExtra("request_id", items.get(position).getId());
            i.putExtra("type_id", items.get(position).getType_id());
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView project_name, request_name, request_type, status, created_by;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            project_name = itemView.findViewById(R.id.project_name);
            request_name = itemView.findViewById(R.id.request_name);
            request_type = itemView.findViewById(R.id.request_type);
            status = itemView.findViewById(R.id.status);
            created_by = itemView.findViewById(R.id.created_by);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }

    public interface AdapterCallback {
        void adapterCallback(String action, int request_id, int type_id);
    }

}