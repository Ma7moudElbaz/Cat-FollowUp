package com.example.followup.job_orders.job_order_details.comments.mentions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.ArrayList;
import java.util.List;


public class UserPresenter extends RecyclerViewPresenter<User> {

    @SuppressWarnings("WeakerAccess")
    protected Adapter adapter;
    ArrayList<User> users;
    @SuppressWarnings("WeakerAccess")
    public UserPresenter(@NonNull Context context,ArrayList<User> users) {
        super(context);
        this.users =users;
    }

    @NonNull
    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        List<User> all = users;
        if (TextUtils.isEmpty(query)) {
            adapter.setData(all);
        } else {
            query = query.toString().toLowerCase();
            List<User> list = new ArrayList<>();
            for (User u : all) {
                if (u.getUsername().toLowerCase().contains(query) ||
                        u.getUsername().toLowerCase().contains(query)) {
                    list.add(u);
                }
            }
            adapter.setData(list);
            Log.e("UserPresenter", "found "+list.size()+" users for query "+query);
        }
        adapter.notifyDataSetChanged();
    }

    protected class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<User> data;

        @SuppressWarnings("WeakerAccess")
        protected class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView username;
            Holder(View itemView) {
                super(itemView);
                root = itemView;
                username = itemView.findViewById(R.id.username);
            }
        }

        @SuppressWarnings("WeakerAccess")
        protected void setData(@Nullable List<User> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (isEmpty()) {
                holder.username.setText("Sorry no user here!");
                holder.root.setOnClickListener(null);
                return;
            }
            final User user = data.get(position);
            holder.username.setText("@" + user.getUsername());
            holder.root.setOnClickListener(v -> dispatchClick(user));
        }
    }
}
