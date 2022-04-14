package com.example.followup.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_forget_password;
import com.example.followup.home.HomeActivity;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends LocalizationActivity implements BottomSheet_forget_password.ForgetPassListener {

    Button signIn;
    TextInputEditText email, password;
    TextView forgot_password;

    private ProgressDialog dialog;

    String device_token = "";

    Webservice ws;


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

    public void login() {
        Map<String, String> map = new HashMap<>();
        final String emailtxt = email.getText().toString();
        final String passwordtxt = password.getText().toString();

        if (emailtxt.length() == 0 || passwordtxt.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.fill_fields, Toast.LENGTH_SHORT).show();
        } else {
            map.put("email", emailtxt);
            map.put("password", passwordtxt);
            dialog.show();
            ws.getApi().login(map).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200) {
                            JSONObject res = new JSONObject(response.body().string());
                            String accessToken = res.getString("access_token");
                            UserUtils.setAccessToken(getBaseContext(), accessToken);
                            UserUtils.setLoginData(getBaseContext(), emailtxt, passwordtxt);
                            setMyData(res.getJSONObject("me"));
                            updateDeviceToken();

                        } else {

                            JSONObject res = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, res.getString("error"), Toast.LENGTH_LONG).show();
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

//                        subscribeToFirebaseTopic(department_id);

            startActivity(new Intent(getBaseContext(), HomeActivity.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void subscribeToFirebaseTopic(int department_id) {

        String topicToSubscribe = "";
        switch (department_id) {
            case 2:
                topicToSubscribe = "nagat";
                break;
            case 3:
                topicToSubscribe = "magdy";
                break;
            case 4:
                topicToSubscribe = "hisham";
                break;
            case 5:
                topicToSubscribe = "sherif";
                break;
            case 6:
                topicToSubscribe = "gamal";
                break;
            case 7:
                topicToSubscribe = "naser";
                break;
            case 9:
                topicToSubscribe = "hossam";
                break;
            case 10:
                topicToSubscribe = "adel";
                break;
            case 11:
                topicToSubscribe = "hazem";
                break;
            case 12:
                topicToSubscribe = "spranza";
                break;
            case 13:
                topicToSubscribe = "hany";
                break;
        }

        if (UserUtils.getSubscribedTopic(getBaseContext()) != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(UserUtils.getSubscribedTopic(getBaseContext()));
        }
        UserUtils.setSubscribedTopic(getBaseContext(), topicToSubscribe);
        FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe);
    }

    public void updateDeviceToken() {
        Map<String, String> map = new HashMap<>();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            device_token = token;
            map.put("device_token", device_token);
            ws.getApi().updateToken(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200) {
                            JSONObject res = new JSONObject(response.body().string());
//                        Toast.makeText(getBaseContext(), res.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {

                        JSONObject res = new JSONObject(response.body().string());
                        Toast.makeText(getBaseContext(), res.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("message"), Toast.LENGTH_LONG).show();
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
}