package com.example.followup.requests.add;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPhotographyActivity extends LocalizationActivity {

    EditText item_name, country, location, days, project_type, camera_type, number_of_cameras, lighting_specify, chroma_specify, props_specify, description, notes;
    Button send_request;
    RadioGroup lighting, chroma, props;
    LinearLayout lighting_layout, chroma_layout, props_layout;
    ImageView back;
    private ProgressDialog dialog;

    String lighting_text = "yes";
    String chroma_text = "yes";
    String props_text = "yes";

    int projectId;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photography);
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
                addPhotography();
            }
        });
    }


    private void initFields() {
        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        projectId = getIntent().getIntExtra("project_id", 0);
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
    }

    private boolean validateFields() {
        if (item_name.length() == 0) {
            item_name.setError("This is required field");
            return false;
        }
        if (country.length() == 0) {
            country.setError("This is required field");
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
        if (camera_type.length() == 0) {
            camera_type.setError("This is required field");
            return false;
        }
        if (number_of_cameras.length() == 0) {
            number_of_cameras.setError("This is required field");
            return false;
        }
        if (description.length() == 0) {
            description.setError("This is required field");
            return false;
        }
        if (notes.length() == 0) {
            notes.setError("This is required field");
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

    private void addPhotography() {
        Map<String, String> map = setPhotographyMap();

        dialog.show();
        ws.getApi().addRequest(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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