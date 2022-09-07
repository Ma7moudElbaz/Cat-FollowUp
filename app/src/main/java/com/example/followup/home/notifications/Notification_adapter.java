package com.example.followup.home.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.followup.R;
import com.example.followup.job_orders.jo_order_details.JobOrderDetailsActivity;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        holder.date.setText(items.get(position).getCreated_at());
        if (items.get(position).getRead_at().equalsIgnoreCase("null")) {
            holder.parent_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_list_item_bg_unread));
            holder.notification_img.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            holder.parent_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_list_item_bg));
            holder.notification_img.setColorFilter(ContextCompat.getColor(mContext, R.color.gray));
        }

        holder.parent_layout.setOnClickListener(v -> {
            Intent i;
            if (items.get(position).getAction_type().equals("request") || items.get(position).getAction_type().equals("cost")) {
                i = new Intent(mContext, RequestDetailsActivity.class);
                i.putExtra("request_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            } else if (items.get(position).getAction_type().equals("jo")) {
                i = new Intent(mContext, JobOrderDetailsActivity.class);
                i.putExtra("job_order_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            } else if (items.get(position).getAction_type().equals("project")) {
                i = new Intent(mContext, RequestsActivity.class);
                i.putExtra("project_id", items.get(position).getAction_id());
                mContext.startActivity(i);
            }

            if (items.get(position).getRead_at().equalsIgnoreCase("null")) {
                readNotification(items.get(position).getNotification_id());
            }

        });

    }

    public void readNotification(String notificationId) {
        WebserviceContext ws  = new WebserviceContext((Activity) mContext);
        Map<String, String> map = new HashMap<>();
        map.put("notification_id", notificationId);
        ws.getApi().readNotification(UserUtils.getAccessToken(mContext), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView message, date;
        final LinearLayout parent_layout;
        final ImageView notification_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            notification_img = itemView.findViewById(R.id.notification_img);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}