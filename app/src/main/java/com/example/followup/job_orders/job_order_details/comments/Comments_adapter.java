package com.example.followup.job_orders.job_order_details.comments;

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

import com.bumptech.glide.Glide;
import com.example.followup.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.List;


public class Comments_adapter extends RecyclerView.Adapter<Comments_adapter.ViewHolder> {

    private final List<Comment_item> items;

    private final Context mContext;

    public Comments_adapter(Context context, ArrayList<Comment_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Comments_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Comments_adapter.ViewHolder holder, final int position) {

        holder.comment.setLinkText(items.get(position).getComment());
        holder.user_name.setText(items.get(position).getUser_name());
        holder.date.setText(items.get(position).getCreated_at());
        Glide.with(mContext)
                .load(items.get(position).getUser_avatar())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.img_profile);

        if (!items.get(position).getAttachment().equals("")) {
            holder.attachment.setVisibility(View.VISIBLE);
        } else {
            holder.attachment.setVisibility(View.GONE);
        }

        holder.attachment.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getAttachment()));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ShapeableImageView img_profile;
        final TextView user_name, date, attachment;
        final SocialTextView comment;

        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_profile = itemView.findViewById(R.id.img_profile);
            user_name = itemView.findViewById(R.id.user_name);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            attachment = itemView.findViewById(R.id.attachment);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}