package com.example.followup.requests.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.StringCheck;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Photography_edit extends LocalizationActivity {

    TextInputEditText item_name, country, location, days, project_type, camera_type, number_of_cameras, lighting_specify, chroma_specify, props_specify, description, notes;
    Button send_request;
    RadioGroup lighting, chroma, props;
    LinearLayout lighting_layout, chroma_layout, props_layout;
    ImageView back;
    private ProgressDialog dialog;

    String lighting_text = "No";
    String chroma_text = "No";
    String props_text = "No";

    int projectId, requestId;

    ProgressBar loading;

    WebserviceContext ws;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photography_edit);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        lighting.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.lighting_yes:
                    lighting_text = "Yes";
                    lighting_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.lighting_no:
                    lighting_text = "No";
                    lighting_layout.setVisibility(View.GONE);
                    break;
            }
        });
        chroma.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chroma_yes:
                    chroma_text = "Yes";
                    chroma_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.chroma_no:
                    chroma_text = "No";
                    chroma_layout.setVisibility(View.GONE);
                    break;
            }
        });
        props.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.props_yes:
                    props_text = "Yes";
                    props_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.props_no:
                    props_text = "No";
                    props_layout.setVisibility(View.GONE);
                    break;
            }
        });

        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                editPhotography();
            }
        });

        getRequestDetails();
    }


    private void initFields() {
        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        projectId = getIntent().getIntExtra("project_id", 0);
        requestId = getIntent().getIntExtra("request_id", 0);
        back = findViewById(R.id.back);
        item_name = findViewById(R.id.item_name);
        country = findViewById(R.id.country);
        location = findViewById(R.id.location);
        days = findViewById(R.id.days);
        project_type = findViewById(R.id.project_type);
        camera_type = findViewById(R.id.camera_type);
        number_of_cameras = findViewById(R.id.number_of_cameras);
        lighting_specify = findViewById(R.id.lighting_specify);
        chroma_specify = findViewById(R.id.chroma_specify);
        props_specify = findViewById(R.id.props_specify);
        description = findViewById(R.id.description);
        notes = findViewById(R.id.notes);

        lighting = findViewById(R.id.lighting);
        chroma = findViewById(R.id.chroma);
        props = findViewById(R.id.props);
        lighting_layout = findViewById(R.id.lighting_layout);
        chroma_layout = findViewById(R.id.chroma_layout);
        props_layout = findViewById(R.id.props_layout);

        send_request = findViewById(R.id.btn_send_request);

        loading = findViewById(R.id.loading);
    }


    private boolean validateFields() {
        if (item_name.length() == 0) {
            item_name.setError("This is required field");
            return false;
        }
        if (location.length() == 0) {
            location.setError("This is required field");
            return false;
        }
        if (days.length() == 0) {
            days.setError("This is required field");
            return false;
        }
        if (project_type.length() == 0) {
            project_type.setError("This is required field");
            return false;
        }
        if (lighting_specify.length() == 0 && lighting_text.equalsIgnoreCase("Yes")) {
            lighting_specify.setError("This is required field");
            return false;
        }
        if (chroma_specify.length() == 0 && chroma_text.equalsIgnoreCase("Yes")) {
            chroma_specify.setError("This is required field");
            return false;
        }
        if (props_specify.length() == 0 && props_text.equalsIgnoreCase("Yes")) {
            props_specify.setError("This is required field");
            return false;
        }
        return true;
    }

    private void getRequestDetails() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getRequestDetails(UserUtils.getAccessToken(getBaseContext()), requestId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    setFields(dataObj);
                    loading.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void setFields(JSONObject dataObj) throws JSONException {
        item_name.setText(StringCheck.returnEmpty(dataObj.getString("item_name")));
        country.setText(StringCheck.returnEmpty(dataObj.getString("country")));
        location.setText(StringCheck.returnEmpty(dataObj.getString("location")));
        days.setText(StringCheck.returnEmpty(dataObj.getString("days")));
        project_type.setText(StringCheck.returnEmpty(dataObj.getString("project_type")));
        camera_type.setText(StringCheck.returnEmpty(dataObj.getString("camera_type")));
        number_of_cameras.setText(StringCheck.returnEmpty(dataObj.getString("number_camera")));
        description.setText(StringCheck.returnEmpty(dataObj.getString("description")));
        notes.setText(StringCheck.returnEmpty(dataObj.getString("note")));

        if (dataObj.getString("lighting").equalsIgnoreCase("null")){
            lighting.check(R.id.lighting_no);
        }else {
            lighting.check(R.id.lighting_yes);
            lighting_specify.setText(dataObj.getString("lighting"));
        }

        if (dataObj.getString("chroma").equalsIgnoreCase("null")){
            chroma.check(R.id.chroma_no);
        }else {
            chroma.check(R.id.chroma_yes);
            chroma_specify.setText(dataObj.getString("chroma"));
        }

        if (dataObj.getString("props").equalsIgnoreCase("null")){
            props.check(R.id.props_no);
        }else {
            props.check(R.id.props_yes);
            props_specify.setText(dataObj.getString("props"));
        }
    }

    private void editPhotography() {
        Map<String, String> map = setPhotographyMap();

        dialog.show();
        ws.getApi().editRequest(UserUtils.getAccessToken(getBaseContext()), requestId, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private Map<String, String> setPhotographyMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "4");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("country", country.getText().toString());
        map.put("location", location.getText().toString());
        map.put("days", days.getText().toString());
        map.put("project_type", project_type.getText().toString());
        map.put("camera_type", camera_type.getText().toString());
        map.put("number_camera", number_of_cameras.getText().toString());
        map.put("description", description.getText().toString());
        map.put("note", notes.getText().toString());
        map.put("quantity", "1");

        if (lighting_text.equalsIgnoreCase("yes")) {
            map.put("lighting", lighting_specify.getText().toString());
        } else {
            map.put("lighting", "");
        }

        if (chroma_text.equalsIgnoreCase("yes")) {
            map.put("chroma", chroma_specify.getText().toString());
        } else {
            map.put("chroma", "");
        }

        if (lighting_text.equalsIgnoreCase("yes")) {
            map.put("props", props_specify.getText().toString());
        } else {
            map.put("props", "");
        }

        return map;
    }
}