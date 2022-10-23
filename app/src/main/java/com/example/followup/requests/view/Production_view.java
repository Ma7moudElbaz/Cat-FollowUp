package com.example.followup.requests.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.followup.R;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.view.attaches.Attach_item;
import com.example.followup.requests.view.attaches.Attaches_adapter;
import com.example.followup.utils.StringCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Production_view extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_production_view, container, false);
    }

    TextView item_name, country, city, venue, days, delivery_date, quantity, dimensions, designer_in_charge, description, notes, screen;


    RecyclerView attach_recycler;
    ArrayList<Attach_item> attaches_list;
    Attaches_adapter attaches_adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
    }

    private void initFields(View view) {
        item_name = view.findViewById(R.id.item_name);
        quantity = view.findViewById(R.id.quantity);
        description = view.findViewById(R.id.description);
        country = view.findViewById(R.id.country);
        city = view.findViewById(R.id.city);
        venue = view.findViewById(R.id.venue);
        days = view.findViewById(R.id.days);
        delivery_date = view.findViewById(R.id.delivery_date);
        dimensions = view.findViewById(R.id.dimensions);
        notes = view.findViewById(R.id.notes);
        designer_in_charge = view.findViewById(R.id.designer_in_charge);
        screen = view.findViewById(R.id.screen);

        attach_recycler = view.findViewById(R.id.attaches_recycler);
        attaches_list = new ArrayList<>();
        initAttachesRecyclerView();


        RequestDetailsActivity activity = (RequestDetailsActivity) getActivity();

        try {
            assert activity != null;
            setFields(activity.getDataObj());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initAttachesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        attach_recycler.setLayoutManager(layoutManager);
        attaches_adapter = new Attaches_adapter(getContext(), attaches_list);
        attach_recycler.setAdapter(attaches_adapter);
    }

    private void setFields(JSONObject dataObj) throws JSONException {
        item_name.setText(StringCheck.returnText(dataObj.getString("item_name")));
        quantity.setText(StringCheck.returnText(dataObj.getString("quantity")));
        description.setText(StringCheck.returnText(dataObj.getString("description")));
        country.setText(StringCheck.returnText(dataObj.getString("country")));
        city.setText(StringCheck.returnText(dataObj.getString("city")));
        venue.setText(StringCheck.returnText(dataObj.getString("venue")));
        days.setText(StringCheck.returnText(dataObj.getString("days")));
        delivery_date.setText(StringCheck.returnText(dataObj.getString("delivery_date")));
        dimensions.setText(StringCheck.returnText(dataObj.getString("dimension")));
        notes.setText(StringCheck.returnText(dataObj.getString("note")));
        designer_in_charge.setText(StringCheck.returnText(dataObj.getString("designer_name")));
        screen.setText(StringCheck.returnText(dataObj.getString("screen")));

        setAttachesList(dataObj.getJSONArray("attach_files"));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAttachesList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final String file_url = currentObject.getString("file");
                final String created_at = currentObject.getString("created_at");

                attaches_list.add(new Attach_item(id, file_url, created_at));

            }

            attaches_adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}