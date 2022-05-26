package com.example.followup.requests.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.followup.R;
import com.example.followup.home.Attach_item;
import com.example.followup.requests.RequestDetailsActivity;
import com.example.followup.requests.view.attaches.Attaches_adapter;

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
        item_name.setText(dataObj.getString("item_name"));
        quantity.setText(dataObj.getString("quantity"));
        description.setText(dataObj.getString("description"));
        country.setText(dataObj.getString("country"));
        city.setText(dataObj.getString("city"));
        venue.setText(dataObj.getString("venue"));
        days.setText(dataObj.getString("days"));
        delivery_date.setText(dataObj.getString("delivery_address"));
        dimensions.setText(dataObj.getString("dimension"));
        notes.setText(dataObj.getString("note"));
        designer_in_charge.setText(dataObj.getString("designer_name"));
        screen.setText(dataObj.getString("screen"));

        setAttachesList(dataObj.getJSONArray("attach_files"));
    }

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