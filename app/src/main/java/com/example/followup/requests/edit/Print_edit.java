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
import android.widget.EditText;
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

public class Print_edit extends LocalizationActivity {
    EditText item_name, quantity, description, pages, paper_weight, colors, di_cut, delivery_address, notes, designer_in_charge;
    Button choose_file, send_request;
    RadioGroup print_type, lamination, binding;
    ImageView back;
    private ProgressDialog dialog;
    LinearLayout color_layout;
    String print_type_text = "Digital";
    String lamination_text = "None";
    String binding_text = "None";
    int projectId, requestId;

    List<String> filesSelected;
    private static final int FILES_REQUEST_CODE = 764546;
    TextView filesChosen;

    ProgressBar loading;

    WebserviceContext ws;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_edit);

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
                editPrint();
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
        description = findViewById(R.id.description);
        pages = findViewById(R.id.pages);
        paper_weight = findViewById(R.id.paper_weight);
        colors = findViewById(R.id.colors);
        color_layout = findViewById(R.id.colors_layout);
        di_cut = findViewById(R.id.di_cut);
        delivery_address = findViewById(R.id.delivery_address);
        notes = findViewById(R.id.notes);
        designer_in_charge = findViewById(R.id.designer_in_charge);
        choose_file = findViewById(R.id.choose_file);
        send_request = findViewById(R.id.btn_send_request);
        print_type = findViewById(R.id.print_type);
        lamination = findViewById(R.id.lamination);
        binding = findViewById(R.id.binding);

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
        if (description.length() == 0) {
            description.setError("This is required field");
            return false;
        }
        if (pages.length() == 0) {
            pages.setError("This is required field");
            return false;
        }
        if (colors.length() == 0 && print_type_text.equalsIgnoreCase("Offset")) {
            colors.setError("This is required field");
            return false;
        }
        if (delivery_address.length() == 0) {
            delivery_address.setError("This is required field");
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
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    setFields(dataObj);
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
        pages.setText(StringCheck.returnEmpty(dataObj.getString("pages")));
        paper_weight.setText(StringCheck.returnEmpty(dataObj.getString("paper_weight")));
        colors.setText(StringCheck.returnEmpty(dataObj.getString("color")));
        di_cut.setText(StringCheck.returnEmpty(dataObj.getString("di_cut")));
        delivery_address.setText(StringCheck.returnEmpty(dataObj.getString("delivery_address")));
        notes.setText(StringCheck.returnEmpty(dataObj.getString("note")));
        designer_in_charge.setText(StringCheck.returnEmpty(dataObj.getString("designer_name")));

        if (dataObj.getString("print_type").equalsIgnoreCase("digital")) {
            print_type.check(R.id.digital);
        } else {
            print_type.check(R.id.offset);
        }

        if (dataObj.getString("lamination").equalsIgnoreCase("matte")) {
            lamination.check(R.id.matte);
        } else if (dataObj.getString("lamination").equalsIgnoreCase("glossy")) {
            lamination.check(R.id.glossy);
        } else {
            lamination.check(R.id.lamination_none);
        }


        if (dataObj.getString("lamination").equalsIgnoreCase("wire")) {
            binding.check(R.id.wire);
        } else if (dataObj.getString("lamination").equalsIgnoreCase("staple")) {
            binding.check(R.id.staple);
        } else {
            binding.check(R.id.binding_none);
        }

    }

    private void editPrint() {
        Map<String, String> map = setPrintMap();

        dialog.show();
        ws.getApi().editRequest(UserUtils.getAccessToken(getBaseContext()), requestId, map).enqueue(new Callback<ResponseBody>() {
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
                        assert response.errorBody() != null;
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
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

    private Map<String, String> setPrintMap() {

        Map<String, String> map = new HashMap<>();
        map.put("type_id", "2");
        map.put("project_id", String.valueOf(projectId));
        map.put("item_name", item_name.getText().toString());
        map.put("quantity", StringCheck.arabicToDecimal(quantity.getText().toString()));
        map.put("description", description.getText().toString());
        map.put("pages", StringCheck.arabicToDecimal(pages.getText().toString()));
        map.put("paper_weight", StringCheck.arabicToDecimal(paper_weight.getText().toString()));
        map.put("color", StringCheck.arabicToDecimal(colors.getText().toString()));
        map.put("di_cut", di_cut.getText().toString());
        map.put("delivery_address", delivery_address.getText().toString());
        map.put("note", notes.getText().toString());
        map.put("designer_name", designer_in_charge.getText().toString());
        map.put("print_type", print_type_text.toLowerCase());
        map.put("lamination", lamination_text.toLowerCase());
        map.put("binding", binding_text.toLowerCase());
        return map;
    }

    public void addRequestAttaches(final String requestId) {
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