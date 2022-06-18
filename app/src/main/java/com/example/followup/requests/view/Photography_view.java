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

public class Photography_view extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photography_view, container, false);
    }

    TextView item_name, country, location, days, project_type, camera_type, number_of_cameras, lighting, chroma, props, description, notes;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
    }

    private void initFields(View view) {
        item_name = view.findViewById(R.id.item_name);
        country = view.findViewById(R.id.country);
        location = view.findViewById(R.id.location);
        days = view.findViewById(R.id.days);
        project_type = view.findViewById(R.id.project_type);
        camera_type = view.findViewById(R.id.camera_type);
        number_of_cameras = view.findViewById(R.id.number_of_cameras);
        lighting = view.findViewById(R.id.lighting);
        chroma = view.findViewById(R.id.chroma);
        props = view.findViewById(R.id.props);
        description = view.findViewById(R.id.description);
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
        item_name.setText(StringCheck.returnText(dataObj.getString("item_name")));
        country.setText(StringCheck.returnText(dataObj.getString("country")));
        location.setText(StringCheck.returnText(dataObj.getString("location")));
        days.setText(StringCheck.returnText(dataObj.getString("days")));
        project_type.setText(StringCheck.returnText(dataObj.getString("project_type")));
        camera_type.setText(StringCheck.returnText(dataObj.getString("camera_type")));
        number_of_cameras.setText(StringCheck.returnText(dataObj.getString("number_camera")));
        lighting.setText(StringCheck.returnText(dataObj.getString("lighting")));
        chroma.setText(StringCheck.returnText(dataObj.getString("chroma")));
        props.setText(StringCheck.returnText(dataObj.getString("props")));
        description.setText(StringCheck.returnText(dataObj.getString("description")));
        notes.setText(StringCheck.returnText(dataObj.getString("note")));
    }
}