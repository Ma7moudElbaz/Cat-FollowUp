package com.example.followup.requests.list.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.list.models.Print_item;
import com.example.followup.utils.UserType;

import java.util.ArrayList;
import java.util.List;


public class Print_adapter extends RecyclerView.Adapter<Print_adapter.ViewHolder> {

    private final List<Print_item> items;

    private final Context mContext;
    private final AdapterCallback mAdapterCallback;

    public Print_adapter(Context context, ArrayList<Print_item> items) {
        this.mAdapterCallback = ((AdapterCallback) context);
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Print_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_print, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Print_adapter.ViewHolder holder, final int position) {

        boolean canEditProject = UserType.canEditProject(mContext, items.get(position).getProject_creator_id(), items.get(position).getProject_assign_id());

        //show action for owner only
        if (!canEditProject || items.get(position).getStatus_code() == 7) {
            holder.show_actions.setVisibility(View.GONE);
        } else {
            holder.show_actions.setVisibility(View.VISIBLE);
        }

        holder.item_name.setText(items.get(position).getItem_name());
        holder.created_by.setText(items.get(position).getCreated_by_name());
        holder.status.setText(String.valueOf(items.get(position).getStatus_message()));
        holder.pages.setText(items.get(position).getPages());
        holder.colors.setText(items.get(position).getColors());
        holder.quantity.setText(String.valueOf(items.get(position).getQuantity()));
        holder.show_actions.setOnClickListener(v -> showPopup(v, position));

        if (items.get(position).isHave_action()){
            holder.have_action.setVisibility(View.VISIBLE);
        }else {
            holder.have_action.setVisibility(View.GONE);
        }

        holder.parent_layout.setOnClickListener(v -> {
            Intent i =new Intent(mContext, RequestDetailsActivity.class);
            i.putExtra("request_id",items.get(position).getId());
            i.putExtra("type_id",items.get(position).getType_id());
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView item_name, created_by, status, pages, colors, quantity,have_action;
        final ImageView show_actions;
        final LinearLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_name);
            created_by = itemView.findViewById(R.id.created_by);
            status = itemView.findViewById(R.id.status);
            pages = itemView.findViewById(R.id.pages);
            colors = itemView.findViewById(R.id.colors);
            quantity = itemView.findViewById(R.id.quantity);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            have_action = itemView.findViewById(R.id.have_action);
            show_actions = itemView.findViewById(R.id.show_actions);

        }
    }

    public interface AdapterCallback {
        void adapterCallback(String action, int request_id, int type_id);
    }


    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.request_actions, popup.getMenu());
        popup.show();
        setMenuData(popup.getMenu(), position);
//        popup.getMenu().getItem(1).setVisible(false);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.request_edit:
                    mAdapterCallback.adapterCallback("edit", items.get(position).getId(), items.get(position).getType_id());
                    return true;
                case R.id.request_cancel:
                    mAdapterCallback.adapterCallback("cancel", items.get(position).getId(), items.get(position).getType_id());
                    return true;
                case R.id.request_delete:
                    mAdapterCallback.adapterCallback("delete", items.get(position).getId(), items.get(position).getType_id());
                    return true;
                case R.id.request_resume:
                    mAdapterCallback.adapterCallback("resume", items.get(position).getId(), items.get(position).getType_id());
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setMenuData(Menu menu, int position) {
        boolean canEditProject = UserType.canEditProject(mContext, items.get(position).getProject_creator_id(), items.get(position).getProject_assign_id());

        if (canEditProject && items.get(position).getStatus_code() == 0) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        } else {

            menu.getItem(2).setVisible(canEditProject && items.get(position).getCost_status_code() == 1);

            if (canEditProject && items.get(position).getStatus_code() != 7) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
            } else {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
            }

        }


    }

}