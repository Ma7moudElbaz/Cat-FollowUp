package com.example.followup.requests.view.attaches;

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


public class Attaches_adapter extends RecyclerView.Adapter<Attaches_adapter.ViewHolder> {

    private final List<Attach_item> items;

    private final Context mContext;

    public Attaches_adapter(Context context, ArrayList<Attach_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attaches, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String textAttachNo = "attach " + (position + 1);
        holder.attach_no.setText(textAttachNo);

        holder.parent_layout.setOnClickListener(v -> {
            Intent i =new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getFile_url()));
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView attach_no;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attach_no = itemView.findViewById(R.id.attach_no);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}