package com.example.followup.supplier_costs.edit;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPurchaseSupplierCostActivity extends LocalizationActivity {

    EditText supplier_name, cost, delivery_date, expiry_date, notes,purchasing_type;
    Button add_cost;
    Spinner currency;
    ImageView back;
    private ProgressDialog dialog;
    DatePickerDialog picker;

    int costId;
    JSONObject dataObj;
    String cost_per_id;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_purchase_supplier_cost);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        delivery_date.setOnClickListener(v -> showDatePicker(delivery_date));
        delivery_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(delivery_date);
            }
        });

        expiry_date.setOnClickListener(v -> showDatePicker(expiry_date));
        expiry_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(expiry_date);
            }
        });

        add_cost.setOnClickListener(v -> {
            if (validateFields()) {
                editCost();
            }
        });
    }


    private void showDatePicker(TextView textview) {
        final Calendar cldr = Calendar.getInstance();
        // date picker dialog
        picker = new DatePickerDialog(EditPurchaseSupplierCostActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    cldr.set(Calendar.YEAR, year);
                    cldr.set(Calendar.MONTH, monthOfYear);
                    cldr.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    textview.setText(sdf.format(cldr.getTime()));
                }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH),
                cldr.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }


    private void setFields(JSONObject dataObj) throws JSONException {
        JSONObject costObj = dataObj.getJSONObject("cost");
        String myCost=costObj.getString("cost");
        String costNum = myCost.substring(0, myCost.indexOf(' '));
        supplier_name.setText(costNum);
        cost.setText(costObj.getString("cost"));
        delivery_date.setText(costObj.getString("delivery_date"));
        expiry_date.setText(costObj.getString("expiry_date"));
        notes.setText(costObj.getString("note"));
        purchasing_type.setText(costObj.getString("purchase_type"));
        cost_per_id = costObj.getString("cost_per_id");
    }

    private void initFields() {
        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        back = findViewById(R.id.back);
        supplier_name = findViewById(R.id.supplier_name);
        cost = findViewById(R.id.cost);
        delivery_date = findViewById(R.id.delivery_date);
        delivery_date.setInputType(InputType.TYPE_NULL);
        expiry_date = findViewById(R.id.expiry_date);
        expiry_date.setInputType(InputType.TYPE_NULL);
        notes = findViewById(R.id.notes);
        currency = findViewById(R.id.currency_spinner);
        purchasing_type = findViewById(R.id.purchasing_type);
        add_cost = findViewById(R.id.btn_add_cost);

        costId = getIntent().getIntExtra("cost_id", 0);
        try {
            dataObj = new JSONObject(getIntent().getStringExtra("dataObj"));
            setFields(dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (supplier_name.length() == 0) {
            supplier_name.setError("This is required field");
            return false;
        }
        if (cost.length() == 0) {
            cost.setError("This is required field");
            return false;
        }
        if (delivery_date.length() == 0) {
            delivery_date.setError("This is required field");
            return false;
        }
        if (expiry_date.length() == 0) {
            expiry_date.setError("This is required field");
            return false;
        }
        if (notes.length() == 0) {
            notes.setError("This is required field");
            return false;
        }
        if (purchasing_type.length() == 0) {
            purchasing_type.setError("This is required field");
            return false;
        }
        return true;
    }

    private void editCost() {
        Map<String, String> map = setCostMap();

        dialog.show();
        ws.getApi().editCost(UserUtils.getAccessToken(getBaseContext()),costId, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Cost Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
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

    private Map<String, String> setCostMap() {
        Map<String, String> map = new HashMap<>();
        map.put("supplier_name", supplier_name.getText().toString());
        map.put("cost", cost.getText().toString());
        map.put("delivery_date", delivery_date.getText().toString());
        map.put("expiry_date", expiry_date.getText().toString());
        map.put("note", notes.getText().toString());
        map.put("currency_id",String.valueOf(currency.getSelectedItemPosition()+1));
        map.put("purchase_type",purchasing_type.getText().toString());
        map.put("cost_per_id", cost_per_id);

        return map;
    }
}