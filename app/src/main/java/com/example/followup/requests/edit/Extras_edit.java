package com.example.followup.requests.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class Extras_edit extends AppCompatActivity {

    TextInputEditText item_name, delivery_address, description;
    Button send_request;

    ImageView back;
    private ProgressDialog dialog;

    int projectId, requestId;

    ProgressBar loading;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extras_edit);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                editExtras();
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
        delivery_address = findViewById(R.id.delivery_address);
        description = findViewById(R.id.description);
        send_request = findViewById(R.id.btn_send_request);

        loading = findViewById(R.id.loading);
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
//        if (delivery_address.length() == 0) {
//            delivery_address.setError("This is required field");
//            return false;
//        }
        return true;
    }

    private void getRequestDetails() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getRequestDetails(UserUtils.getAccessToken(getBaseContext()), requestId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    setFields(dataObj);
//                    projectId = dataObj.getInt("project_id");
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
        delivery_address.setText(StringCheck.returnEmpty(dataObj.getString("delivery_address")));
        description.setText(StringCheck.returnEmpty(dataObj.getString("description")));

    }

    private void editExtras() {
        Map<String, String> map = setExtrasMap();

        dialog.show();
        ws.getApi().editRequest(UserUtils.getAccessToken(getBaseContext()), requestId, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Request Updated successfully", Toast.LENGTH_LONG).show();
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
        map.put("delivery_address", delivery_address.getText().toString());
        map.put("description", description.getText().toString());
        map.put("quantity", "1");


        return map;
    }
}