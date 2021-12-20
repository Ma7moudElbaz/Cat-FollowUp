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
import com.example.followup.webservice.Webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPrintActivity extends LocalizationActivity {

    EditText item_name, quantity, description, pages, paper_weight, colors, di_cut, delivery_address, notes, designer_in_charge;
    Button choose_file, send_request;
    RadioGroup print_type, lamination, binding;
    ImageView back;
    private ProgressDialog dialog;
    LinearLayout color_layout;


    String print_type_text = "Digital";
    String lamination_text = "Matte";
    String binding_text = "Wire";

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_print);
        initFields();
        back.setOnClickListener(v -> onBackPressed());

        print_type.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.digital:
                    print_type_text = "Digital";
                    color_layout.setVisibility(View.GONE);
                    break;
                case R.id.offset:
                    print_type_text = "Offset";
                    color_layout.setVisibility(View.VISIBLE);
                    break;
            }
        });

        lamination.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.matte:
                    lamination_text = "Matte";
                    break;
                case R.id.glossy:
                    lamination_text = "Glossy";
                    break;
                case R.id.lamination_none:
                    lamination_text = "None";
                    break;
            }
        });

        binding.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.wire:
                    binding_text = "Wire";
                    break;
                case R.id.staple:
                    binding_text = "Staple";
                    break;
                case R.id.binding_none:
                    binding_text = "None";
                    break;
            }
        });

        send_request.setOnClickListener(v -> {
            if (validateFields()) {
                addPrint();
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
        description = findViewById(R.id.description);
        pages = findViewById(R.id.pages);
        paper_weight = findViewById(R.id.paper_weight);
        colors = findViewById(R.id.colors);
        color_layout = findViewById(R.id.color_layout);
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
        if (colors.length() == 0 && print_type_text.equalsIgnoreCase("Offset")) {
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

    private void addPrint() {
        Map<String, String> map = setPrintMap();

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

    private Map<String, String> setPrintMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "2");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("quantity", quantity.getText().toString());
        map.put("description", description.getText().toString());
        map.put("pages", pages.getText().toString());
        map.put("paper_weight", paper_weight.getText().toString());
        map.put("color", colors.getText().toString());
        map.put("di_cut", di_cut.getText().toString());
        map.put("delivery_address", delivery_address.getText().toString());
        map.put("note", notes.getText().toString());
        map.put("designer_name", designer_in_charge.getText().toString());
        map.put("print_type", print_type_text.toLowerCase());
        map.put("lamination", lamination_text.toLowerCase());
        map.put("binding", binding_text.toLowerCase());
        return map;
    }

}