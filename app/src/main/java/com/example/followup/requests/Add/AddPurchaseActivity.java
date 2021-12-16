package com.example.followup.requests.Add;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPurchaseActivity extends LocalizationActivity {

    EditText item_name, quantity, color, material, description, brand, delivery_address, notes;
    Button choose_file, send_request;
    RadioGroup branding;
    ImageView back;
    private ProgressDialog dialog;
    LinearLayout brand_layout;


    String branding_text = "yes";

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        branding.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.branding_yes:
                    branding_text = "Yes";
                    brand_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.branding_no:
                    branding_text = "No";
                    brand_layout.setVisibility(View.GONE);
                    break;
            }
        });


        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                addPurchase();
            }
        });
    }

    private void initFields() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        projectId = getIntent().getIntExtra("project_id", 0);
        back = findViewById(R.id.back);
        item_name = findViewById(R.id.item_name);
        quantity = findViewById(R.id.quantity);
        color = findViewById(R.id.color);
        material = findViewById(R.id.material);
        description = findViewById(R.id.description);
        branding = findViewById(R.id.branding);
        brand = findViewById(R.id.brand);
        brand_layout = findViewById(R.id.brand_layout);
        delivery_address = findViewById(R.id.delivery_address);
        notes = findViewById(R.id.notes);
        choose_file = findViewById(R.id.choose_file);
        send_request = findViewById(R.id.btn_send_request);
    }

    private boolean validateFields() {
        if (item_name.length() == 0) {
            item_name.setError("This is required field");
            return false;
        }
        if (quantity.length() == 0) {
            quantity.setError("This is required field");
            return false;
        }
        if (color.length() == 0) {
            color.setError("This is required field");
            return false;
        }
        if (material.length() == 0) {
            material.setError("This is required field");
            return false;
        }
        if (description.length() == 0) {
            description.setError("This is required field");
            return false;
        }
        if (brand.length() == 0 && branding_text.equalsIgnoreCase("Yes")) {
            brand.setError("This is required field");
            return false;
        }
        if (delivery_address.length() == 0) {
            delivery_address.setError("This is required field");
            return false;
        }
        if (notes.length() == 0) {
            notes.setError("This is required field");
            return false;
        }
        return true;
    }

    private void addPurchase() {
        Map<String, String> map = setPurchaseMap();

        dialog.show();
        Webservice.getInstance().getApi().addRequest(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

                    } else {
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
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

    private Map<String, String> setPurchaseMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "1");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("quantity", quantity.getText().toString());
        map.put("color", color.getText().toString());
        map.put("material", material.getText().toString());
        map.put("description", description.getText().toString());
        map.put("delivery_address", delivery_address.getText().toString());
        map.put("note", notes.getText().toString());

        if (branding_text.equalsIgnoreCase("yes")){
            map.put("brand", brand.getText().toString());
        }else {
            map.put("brand", "");
        }
        return map;
    }
}