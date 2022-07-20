package com.example.followup.requests.add;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.followup.R;
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

public class AddExtrasActivity extends AppCompatActivity {
    TextInputEditText item_name, address, description;
    Button send_request;

    ImageView back;
    private ProgressDialog dialog;

    int projectId;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_extras);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                addExtras();
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
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);

        send_request = findViewById(R.id.btn_send_request);
    }

    private boolean validateFields() {
        if (item_name.length() == 0) {
            item_name.setError("This is required field");
            return false;
        }
        if (description.length() == 0) {
            description.setError("This is required field");
            return false;
        }
//        if (address.length() == 0) {
//            address.setError("This is required field");
//            return false;
//        }
        return true;
    }

    private void addExtras() {
        Map<String, String> map = setExtrasMap();

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

    private Map<String, String> setExtrasMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "5");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("delivery_address", address.getText().toString());
        map.put("description", description.getText().toString());
        map.put("quantity", "1");

        return map;
    }
}