package com.example.followup.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.BuildConfig;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_forget_password;
import com.example.followup.home.HomeActivity;
import com.example.followup.utils.Constants;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends LocalizationActivity implements BottomSheet_forget_password.ForgetPassListener {
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
    }

    Button signIn;
    TextInputEditText email, password;
    TextView forgot_password;

    private ProgressDialog dialog;

    String device_token = "";

    Webservice ws;
     final String app_link = Constants.APP_LINK;

    public void showForgetPassSheet() {
        BottomSheet_forget_password langBottomSheet =
                new BottomSheet_forget_password(LoginActivity.this);
        langBottomSheet.show(getSupportFragmentManager(), "requests_filter");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFields();

        email.setText(UserUtils.getLoginName(getBaseContext()));
        password.setText(UserUtils.getLoginPassword(getBaseContext()));

        forgot_password.setOnClickListener(v -> showForgetPassSheet());
        signIn.setOnClickListener(v -> login());
        checkAppVersion();
    }

    private void initFields() {
        ws = new Webservice();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgot_password = findViewById(R.id.forgot_password);
        signIn = findViewById(R.id.btn_sign_in);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

    }

    public void checkAppVersion() {
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/javiersantos/AppUpdater/master/app/update-changelog.json")
                .start();

//        String versionName = BuildConfig.VERSION_NAME;

//        ws.getApi().checkAppVersion(versionName).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                if (!response.isSuccessful()) {
//                    showUpdateDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void showUpdateDialog() {
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Update Required")
                .setPositiveButton("Update", null)
                .setCancelable(false)
                .show();

        Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        updateButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app_link))));


    }

    public void login() {
        Map<String, String> map = new HashMap<>();
        final String emailTxt = Objects.requireNonNull(email.getText()).toString();
        final String passwordTxt = Objects.requireNonNull(password.getText()).toString();

        if (emailTxt.length() == 0 || passwordTxt.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.fill_fields, Toast.LENGTH_SHORT).show();
        } else {
            map.put("email", emailTxt);
            map.put("password", passwordTxt);
            dialog.show();
            ws.getApi().login(map).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            JSONObject res = new JSONObject(response.body().string());
                            String accessToken = res.getString("access_token");
                            UserUtils.setAccessToken(getBaseContext(), accessToken);
                            UserUtils.setLoginData(getBaseContext(), emailTxt, passwordTxt);
                            setMyData(res.getJSONObject("me"));
                            updateDeviceToken();

                        } else {

                            assert response.errorBody() != null;
                            JSONObject res = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, res.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    public void setMyData(JSONObject res) {
        try {
            if (res.getString("children_id").equals("null")) {
                UserUtils.setChildId(getBaseContext(), 0);
            } else {
                UserUtils.setChildId(getBaseContext(), res.getInt("children_id"));
            }
            UserUtils.setParentId(getBaseContext(), res.getInt("parent_id"));
            UserUtils.setUserId(getBaseContext(), res.getInt("id"));
            UserUtils.setUserName(getBaseContext(), res.getString("name"));
            UserUtils.setUserEmail(getBaseContext(), res.getString("email"));
            UserUtils.setCountryId(getBaseContext(),res.getInt("country_id"));

//                        subscribeToFirebaseTopic(department_id);

            Intent i = new Intent(getBaseContext(), HomeActivity.class);
            i.putExtra("fromActivity", "login");
            startActivity(i);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateDeviceToken() {
        Map<String, String> map = new HashMap<>();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            device_token = token;
            Log.e("Fire Base Device Token", device_token);
            map.put("device_token", device_token);
            ws.getApi().updateToken(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (!response.isSuccessful()) {
                            assert response.errorBody() != null;
                            Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    @Override
    public void forgetPassword(String email) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        dialog.show();
        ws.getApi().forgetPassword(map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        JSONObject res = new JSONObject(response.body().string());
                        Toast.makeText(getBaseContext(), res.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}