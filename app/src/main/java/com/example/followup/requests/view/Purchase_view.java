package com.example.followup.requests.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.followup.R;
import com.example.followup.requests.RequestDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Purchase_view extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase_view, container, false);
    }

    TextView item_name, quantity, color, material, description, brand, delivery_address, notes;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
    }

    private void initFields(View view) {
        item_name = view.findViewById(R.id.item_name);
        quantity = view.findViewById(R.id.quantity);
        description = view.findViewById(R.id.description);
        color = view.findViewById(R.id.color);
        material = view.findViewById(R.id.material);
        brand = view.findViewById(R.id.brand);
        delivery_address = view.findViewById(R.id.delivery_address);
        notes = view.findViewById(R.id.notes);

        RequestDetailsActivity activity = (RequestDetailsActivity) getActivity();

        try {
            assert activity != null;
            setFields(activity.getDataObj());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setFields(JSONObject dataObj) throws JSONException {
        item_name.setText(dataObj.getString("item_name"));
        quantity.setText(dataObj.getString("quantity"));
        description.setText(dataObj.getString("description"));
        color.setText(dataObj.getString("color"));
        material.setText(dataObj.getString("material"));
        brand.setText(dataObj.getString("brand"));
        delivery_address.setText(dataObj.getString("delivery_address"));
        notes.setText(dataObj.getString("note"));

    }
}