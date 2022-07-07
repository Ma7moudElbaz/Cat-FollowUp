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
import com.example.followup.utils.StringCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class Extras_view extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extras_view, container, false);
    }

    TextView item_name, delivery_address, description;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
    }


    private void initFields(View view) {
        item_name = view.findViewById(R.id.item_name);
        delivery_address = view.findViewById(R.id.delivery_address);
        description = view.findViewById(R.id.description);


        RequestDetailsActivity activity = (RequestDetailsActivity) getActivity();

        try {
            assert activity != null;
            setFields(activity.getDataObj());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setFields(JSONObject dataObj) throws JSONException {
        item_name.setText(StringCheck.returnText(dataObj.getString("item_name")));
        delivery_address.setText(StringCheck.returnText(dataObj.getString("delivery_address")));
        description.setText(StringCheck.returnText(dataObj.getString("description")));
    }
}