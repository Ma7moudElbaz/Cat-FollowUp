package com.example.followup.requests.Add;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;

public class AddPrintActivity extends LocalizationActivity {

    TextView item_name,quantity,description,pages,paper_weight,colors,di_cut,delivery_address
    ,notes,designer_in_charge;
    Button choose_file,send_request;
    RadioGroup print_type,lamination,binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_print);
        initFields();
    }

    private void initFields() {
        item_name = findViewById(R.id.item_name);
        quantity = findViewById(R.id.quantity);
        description = findViewById(R.id.description);
        pages = findViewById(R.id.pages);
        paper_weight = findViewById(R.id.paper_weight);
        colors = findViewById(R.id.colors);
        di_cut = findViewById(R.id.di_cut);
        delivery_address = findViewById(R.id.delivery_address);
        notes = findViewById(R.id.notes);
        designer_in_charge = findViewById(R.id.designer_in_charge);
        choose_file = findViewById(R.id.choose_file);
        send_request = findViewById(R.id.btn_send_request);
        print_type = findViewById(R.id.print_type);
        lamination = findViewById(R.id.lamination);
        binding = findViewById(R.id.binding);
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
        if (description.length() == 0) {
            description.setError("This is required field");
            return false;
        }
        if (pages.length() == 0) {
            pages.setError("This is required field");
            return false;
        }
        if (paper_weight.length() == 0) {
            paper_weight.setError("This is required field");
            return false;
        }
        if (colors.length() == 0) {
            colors.setError("This is required field");
            return false;
        }
        if (di_cut.length() == 0) {
            di_cut.setError("This is required field");
            return false;
        }
        if (delivery_address.length() == 0) {
            colors.setError("This is required field");
            return false;
        }
        if (notes.length() == 0) {
            colors.setError("This is required field");
            return false;
        }
        if (designer_in_charge.length() == 0) {
            colors.setError("This is required field");
            return false;
        }
        return true;
    }

}