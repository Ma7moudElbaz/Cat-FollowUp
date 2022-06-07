package com.example.followup.supplier_costs.edit;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.RealPathUtil;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.jem.fliptabs.FlipTab;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPurchaseSupplierCostActivity extends LocalizationActivity {

    EditText supplier_name, cost, delivery_date, expiry_date, notes, purchasing_type;
    Button add_cost;
    Spinner currency;
    ImageView back;
    private ProgressDialog dialog;
    DatePickerDialog picker;

    int costId;
    JSONObject dataObj;

    WebserviceContext ws;

    List<String> filesSelected;
    private static final int FILES_REQUEST_CODE = 764546;
    Button choose_file;
    TextView filesChosen;

    FlipTab cost_per_switch;
    String cost_per_id;

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

        cost_per_switch.setTabSelectedListener(new FlipTab.TabSelectedListener() {
            @Override
            public void onTabSelected(boolean isLeftTab, @NonNull String s) {
                if (isLeftTab) {
                    cost_per_id = "1";
                } else {
                    cost_per_id = "2";
                }
            }

            @Override
            public void onTabReselected(boolean isLeftTab, @NonNull String s) {

            }
        });

        choose_file.setOnClickListener(v -> {

            Dexter.withContext(getBaseContext())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                pickFromGallery();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        }
                    }).check();
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
        supplier_name.setText(costObj.getString("supplier_name"));
        cost.setText(costObj.getString("cost_name"));
        delivery_date.setText(costObj.getString("delivery_date"));
        expiry_date.setText(costObj.getString("expiry_date"));
        notes.setText(costObj.getString("note"));
        purchasing_type.setText(costObj.getString("purchase_type"));

        cost_per_id = costObj.getString("cost_per_id");
        if (cost_per_id.equals("1")) {
            cost_per_switch.selectLeftTab(false);
        } else {
            cost_per_switch.selectRightTab(false);
        }
    }

    private void initFields() {

        cost_per_switch = findViewById(R.id.cost_per_switch);

        filesSelected = new ArrayList<>();
        filesChosen = findViewById(R.id.files_chosen);
        choose_file = findViewById(R.id.choose_file);


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
        Map<String, RequestBody> map = setCostMapRequestBody();
        List<MultipartBody.Part> fileToUpload = addAttaches(filesSelected);

        dialog.show();
        ws.getApi().editCost(UserUtils.getAccessToken(getBaseContext()), costId, fileToUpload, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Cost Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
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

    private Map<String, RequestBody> setCostMapRequestBody() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("supplier_name", RequestBody.create(MediaType.parse("text/plain"), supplier_name.getText().toString()));
        map.put("cost", RequestBody.create(MediaType.parse("text/plain"), cost.getText().toString()));
        map.put("delivery_date", RequestBody.create(MediaType.parse("text/plain"), delivery_date.getText().toString()));
        map.put("expiry_date", RequestBody.create(MediaType.parse("text/plain"), expiry_date.getText().toString()));
        map.put("note", RequestBody.create(MediaType.parse("text/plain"), notes.getText().toString()));
        map.put("currency_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currency.getSelectedItemPosition() + 1)));
        map.put("purchase_type", RequestBody.create(MediaType.parse("text/plain"), purchasing_type.getText().toString()));
        map.put("cost_per_id", RequestBody.create(MediaType.parse("text/plain"), cost_per_id));

        return map;
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivityForResult(intent, FILES_REQUEST_CODE);
    }


    private void pickFromFiles() {

        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("*/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
//        String[] mimeTypes = {"image/jpeg", "image/png"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivityForResult(intent, FILES_REQUEST_CODE);
    }

    private List<MultipartBody.Part> addAttaches(List<String> files) {
        List<MultipartBody.Part> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("reference", file.getName(), fileReqBody);
            list.add(part);
        }
        return list;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case FILES_REQUEST_CODE:
                    filesSelected.clear();
                    //data.getData returns the content URI for the selected files
                    if (data == null) {
                        return;
                    } else if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                        }
                    } else {
                        Uri uri = data.getData();
                        filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                    }
                    filesChosen.setText(filesSelected.size() + " Files Selected");

                    Log.e("Data selected", filesSelected.toString());
            }
    }
}