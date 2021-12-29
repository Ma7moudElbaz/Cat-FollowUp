package com.example.followup.requests.add;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductionActivity extends LocalizationActivity {

    EditText item_name, country, venue, days,delivery_date , quantity, dimensions, designer_in_charge,description,notes,screen_specs;
    Button choose_file, send_request;
    LinearLayout screen_specs_container;
    RadioGroup screen;
    ImageView back;
    private ProgressDialog dialog;
    DatePickerDialog picker;

    String screen_text = "yes";

    int projectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_production);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        delivery_date.setOnClickListener(v -> showDatePicker(delivery_date));
        delivery_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(delivery_date);
            }
        });

        screen.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.screen_yes:
                    screen_text = "Yes";
                    screen_specs_container.setVisibility(View.VISIBLE);
                    break;
                case R.id.screen_no:
                    screen_text = "No";
                    screen_specs_container.setVisibility(View.GONE);
                    break;
            }
        });

        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                addProduction();
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
        country = findViewById(R.id.country);
        venue = findViewById(R.id.venue);
        days = findViewById(R.id.days);
        delivery_date = findViewById(R.id.delivery_date);
        delivery_date.setInputType(InputType.TYPE_NULL);
        quantity = findViewById(R.id.quantity);
        dimensions = findViewById(R.id.dimensions);
        description = findViewById(R.id.description);
        notes = findViewById(R.id.notes);
        designer_in_charge = findViewById(R.id.designer_in_charge);
        screen = findViewById(R.id.screen);
        screen_specs = findViewById(R.id.screen_specs);
        screen_specs_container = findViewById(R.id.screen_specs_container);

        choose_file = findViewById(R.id.choose_file);
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
        if (venue.length() == 0) {
            venue.setError("This is required field");
            return false;
        }
        if (days.length() == 0) {
            days.setError("This is required field");
            return false;
        }
        if (delivery_date.length() == 0) {
            delivery_date.setError("This is required field");
            return false;
        }
        if (quantity.length() == 0) {
            quantity.setError("This is required field");
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
        if (designer_in_charge.length() == 0) {
            designer_in_charge.setError("This is required field");
            return false;
        }
        if (screen_specs.length() == 0&&screen_text.equalsIgnoreCase("yes")) {
            screen_specs.setError("This is required field");
            return false;
        }
        return true;
    }

    private void addProduction() {
        Map<String, String> map = setProductionMap();

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

    private Map<String, String> setProductionMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "3");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("country", country.getText().toString());
        map.put("venue", venue.getText().toString());
        map.put("days", days.getText().toString());
        map.put("delivery_date", delivery_date.getText().toString());
        map.put("quantity", quantity.getText().toString());
        map.put("dimension", dimensions.getText().toString());
        map.put("designer_name", designer_in_charge.getText().toString());
        map.put("description", description.getText().toString());
        map.put("note", notes.getText().toString());
        map.put("screen", screen_specs.getText().toString());

        return map;
    }

    private void showDatePicker(TextView textview) {

        final Calendar cldr = Calendar.getInstance();
        // date picker dialog
        picker = new DatePickerDialog(AddProductionActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    cldr.set(Calendar.YEAR, year);
                    cldr.set(Calendar.MONTH, monthOfYear);
                    cldr.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "yy-MM-dd"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    textview.setText(sdf.format(cldr.getTime()));
                }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH),
                cldr.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }
}