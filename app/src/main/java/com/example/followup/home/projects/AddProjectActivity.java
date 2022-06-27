package com.example.followup.home.projects;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_companies_from_activity;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProjectActivity extends LocalizationActivity implements BottomSheet_companies_from_activity.SelectedCompanyListener {

    ImageView back;
    EditText project_name, client_name, country, project_timeLine;
    Spinner sales_contact;
    TextView client_company;
    LinearLayout sales_contact_layout;
    CheckBox manage_myself;
    Button add;

    private ProgressDialog dialog;
    DatePickerDialog picker;
    List<String> sales_names = new ArrayList<>();
    List<String> sales_Ids = new ArrayList<>();


    String selectedCompanyId = "0";

    WebserviceContext ws;

    public void showCompaniesBottomSheet() {
        BottomSheet_companies_from_activity companiesBottomSheet =
                new BottomSheet_companies_from_activity(AddProjectActivity.this);
        companiesBottomSheet.show(getSupportFragmentManager(), "companies");
    }

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

        client_company.setOnClickListener(v -> showCompaniesBottomSheet());

        add.setOnClickListener(v -> {
            if (validateFields()) {
                addProject();
            }
        });
        getMyTeam();
    }

    private void getMyTeam() {

        dialog.show();
        ws.getApi().getMyTeam(UserUtils.getAccessToken(getBaseContext())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray teamArray = responseObject.getJSONArray("data");
                        for (int i = 0; i < teamArray.length(); i++) {
                            JSONObject currentObject = teamArray.getJSONObject(i);
                            sales_names.add(currentObject.getString("name"));
                            sales_Ids.add(currentObject.getString("id"));
                        }

                    }
                    setSalesSpinner();
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

    private void addProject() {
        Map<String, String> map = setProjectMap();

        dialog.show();
        ws.getApi().addProject(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
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

    private Map<String, String> setProjectMap() {
        Map<String, String> map = new HashMap<>();
        map.put("company_id", selectedCompanyId);
        map.put("project_name", project_name.getText().toString());
        map.put("client_name", client_name.getText().toString());
        map.put("project_country", country.getText().toString());
        map.put("project_timeline", project_timeLine.getText().toString());
        if (manage_myself.isChecked()) {
            map.put("assign_to", "");
        } else {
            map.put("assign_to", sales_Ids.get(sales_contact.getSelectedItemPosition()));
        }
        return map;
    }

    private void initFields() {
        ws = new WebserviceContext(this);
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

        sales_names.add("Select Sales");
        sales_Ids.add("0");
    }

    private void setSalesSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, sales_names);
        sales_contact.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateFields() {
        if (client_company.getText().toString().equals("Select Company")) {
            Toast.makeText(getBaseContext(), "Select Company", Toast.LENGTH_SHORT).show();
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
        if (sales_contact.getSelectedItemPosition() == 0 && !manage_myself.isChecked()) {
            Toast.makeText(getBaseContext(), "Select sales contact", Toast.LENGTH_SHORT).show();
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

                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    textview.setText(sdf.format(cldr.getTime()));
                }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH),
                cldr.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    @Override
    public void selectedCompany(String companyName, String companyId) {
        client_company.setText(companyName);
        selectedCompanyId = companyId;
    }
}