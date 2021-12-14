package com.example.followup.home.projects;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

import org.w3c.dom.Text;

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

public class AddProjectActivity extends LocalizationActivity {

    ImageView back;
    EditText client_company, project_name, client_name, country, project_timeLine, sales_contact;
    LinearLayout sales_contact_layout;
    CheckBox manage_myself;
    Button add;

    private ProgressDialog dialog;
    DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        initFields();
        setSalesContactVisibility(manage_myself.isChecked());
        manage_myself.setOnCheckedChangeListener((buttonView, isChecked) -> setSalesContactVisibility(isChecked));
        back.setOnClickListener(v -> onBackPressed());
        project_timeLine.setOnClickListener(v -> showDatePicker(project_timeLine));
        project_timeLine.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(project_timeLine);
            }
        });


        add.setOnClickListener(v -> {
            if (validateFields()) {
                addProject();
            }
        });

    }

    private void addProject() {
        Map<String, String> map = setProjectMap();

        dialog.show();
        Webservice.getInstance().getApi().addProject(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
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

    private Map<String, String> setProjectMap() {
        Map<String, String> map = new HashMap<>();
        map.put("client_company", client_company.getText().toString());
        map.put("project_name", project_name.getText().toString());
        map.put("client_name", client_name.getText().toString());
        map.put("project_country", country.getText().toString());
        map.put("project_timeline", project_timeLine.getText().toString());
        if (manage_myself.isChecked()) {
            map.put("assign_to", "");
        } else {
            map.put("assign_to", sales_contact.getText().toString());
        }
        return map;
    }

    private void initFields() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        back = findViewById(R.id.back);
        client_company = findViewById(R.id.client_company);
        project_name = findViewById(R.id.project_name);
        client_name = findViewById(R.id.client_name);
        country = findViewById(R.id.country);
        project_timeLine = findViewById(R.id.project_timeLine);
        project_timeLine.setInputType(InputType.TYPE_NULL);

        sales_contact = findViewById(R.id.sales_contact);
        sales_contact_layout = findViewById(R.id.sales_contact_layout);
        manage_myself = findViewById(R.id.manage_myself);
        add = findViewById(R.id.add);
    }

    private boolean validateFields() {
        if (client_company.length() == 0) {
            client_company.setError("This is required field");
            return false;
        }
        if (project_name.length() == 0) {
            project_name.setError("This is required field");
            return false;
        }
        if (client_name.length() == 0) {
            client_name.setError("This is required field");
            return false;
        }
        if (country.length() == 0) {
            country.setError("This is required field");
            return false;
        }
        if (project_timeLine.length() == 0) {
            project_timeLine.setError("This is required field");
            return false;
        }
        if (sales_contact.length() == 0 && !manage_myself.isChecked()) {
            sales_contact.setError("This is required field");
            return false;
        }
        return true;
    }

    private void setSalesContactVisibility(boolean isChecked) {
        if (isChecked) {
            sales_contact_layout.setVisibility(View.GONE);
        } else {
            sales_contact_layout.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePicker(TextView textview) {

        final Calendar cldr = Calendar.getInstance();
        // date picker dialog
        picker = new DatePickerDialog(AddProjectActivity.this,
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