package com.example.followup.home.projects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;

public class AddProjectActivity extends LocalizationActivity {

    ImageView back;
    EditText client_company, project_name, client_name, country, project_timeLine, sales_contact;
    CheckBox manage_myself;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        initFields();

        back.setOnClickListener(v -> onBackPressed());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
                if (validateFields())
                    Toast.makeText(getBaseContext(), "Validated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFields() {
        back = findViewById(R.id.back);
        client_company = findViewById(R.id.client_company);
        project_name = findViewById(R.id.project_name);
        client_name = findViewById(R.id.client_name);
        country = findViewById(R.id.country);
        project_timeLine = findViewById(R.id.project_timeLine);
        sales_contact = findViewById(R.id.sales_contact);
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
        if (sales_contact.length() == 0) {
            sales_contact.setError("This is required field");
            return false;
        }
        return true;
    }
}