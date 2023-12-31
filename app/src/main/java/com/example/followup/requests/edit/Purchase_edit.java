package com.example.followup.requests.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.RealPathUtil;
import com.example.followup.utils.StringCheck;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Purchase_edit extends LocalizationActivity {

    TextInputEditText item_name, quantity, color, material, description, brand, delivery_address, notes;
    Button choose_file, send_request;
    RadioGroup branding;
    ImageView back;
    private ProgressDialog dialog;
    LinearLayout brand_layout;
    String branding_text = "Yes";
    int projectId,requestId;

    List<String> filesSelected;
    private static final int FILES_REQUEST_CODE = 764546;
    TextView filesChosen;

    ProgressBar loading;

    WebserviceContext ws;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_edit);

        initFields();

        back.setOnClickListener(v -> onBackPressed());

        choose_file.setOnClickListener(v -> Dexter.withContext(getBaseContext())
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
                }).check());

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
                editPurchase();
            }
        });


        getRequestDetails();
    }

    private void initFields() {
        ws = new WebserviceContext(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        filesSelected = new ArrayList<>();
        filesChosen = findViewById(R.id.files_chosen);

        projectId = getIntent().getIntExtra("project_id", 0);
        requestId = getIntent().getIntExtra("request_id", 0);
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


        loading = findViewById(R.id.loading);
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
        if (delivery_address.length() == 0) {
            delivery_address.setError("This is required field");
            return false;
        }
        if (brand.length() == 0 && branding_text.equalsIgnoreCase("Yes")) {
            brand.setError("This is required field");
            return false;
        }
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
        quantity.setText(StringCheck.returnEmpty(dataObj.getString("quantity")));
        description.setText(StringCheck.returnEmpty(dataObj.getString("description")));
        color.setText(StringCheck.returnEmpty(dataObj.getString("color")));
        material.setText(StringCheck.returnEmpty(dataObj.getString("material")));

        if (dataObj.getString("brand").equalsIgnoreCase("null")){
            branding.check(R.id.branding_no);
        }else {
            branding.check(R.id.branding_yes);
            brand.setText(dataObj.getString("brand"));
        }
        delivery_address.setText(StringCheck.returnEmpty(dataObj.getString("delivery_address")));
        notes.setText(StringCheck.returnEmpty(dataObj.getString("note")));
    }

    private void editPurchase() {
        Map<String, String> map = setPurchaseMap();

        dialog.show();
        ws.getApi().editRequest(UserUtils.getAccessToken(getBaseContext()),requestId, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        if (filesSelected.size() != 0) {
                            addRequestAttaches(responseObject.getJSONObject("data").getString("id"));
                        } else {
                            Toast.makeText(getBaseContext(), "Request Updated Successfully", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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
        map.put("quantity", StringCheck.arabicToDecimal(quantity.getText().toString()));
        map.put("color", StringCheck.arabicToDecimal(color.getText().toString()));
        map.put("material", material.getText().toString());
        map.put("description", description.getText().toString());
        map.put("delivery_address", delivery_address.getText().toString());
        map.put("note", notes.getText().toString());

        if (branding_text.equalsIgnoreCase("yes")) {
            map.put("brand", brand.getText().toString());
        } else {
            map.put("brand", "");
        }
        return map;
    }


    public void addRequestAttaches(final String requestId) {
        dialog.show();

        List<MultipartBody.Part> fileToUpload = addAttaches(filesSelected);
        RequestBody request_id = RequestBody.create(MediaType.parse("text/plain"), requestId);

        ws.getApi().addAttach(UserUtils.getAccessToken(getBaseContext()), fileToUpload, request_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Request Added successfully but attachment failed", Toast.LENGTH_LONG).show();
                    }

                dialog.dismiss();
                onBackPressed();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                Log.d("Request failure", call + " , " + t.getMessage());
                dialog.dismiss();
                onBackPressed();
            }
        });
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
            MultipartBody.Part part = MultipartBody.Part.createFormData("attaches[]", file.getName(), fileReqBody);
            list.add(part);
        }
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == FILES_REQUEST_CODE) {
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