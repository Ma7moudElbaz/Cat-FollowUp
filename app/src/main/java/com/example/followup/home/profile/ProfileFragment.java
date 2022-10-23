package com.example.followup.home.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_change_password;
import com.example.followup.login.LoginActivity;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements BottomSheet_choose_change_password.ChangePassListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void showChangePassSheet() {
        BottomSheet_choose_change_password langBottomSheet =
                new BottomSheet_choose_change_password(ProfileFragment.this);
        langBottomSheet.show(getParentFragmentManager(), "requests_filter");
    }

    TextView name, email;
    Button changePassword, logOut;
    private ProgressDialog dialog;
    WebserviceContext ws;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        changePassword.setOnClickListener(v -> showChangePassSheet());
        name.setText(UserUtils.getUserName(getContext()));
        email.setText(UserUtils.getUserEmail(getContext()));
        logOut.setOnClickListener(v -> {
            logout();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        changePassword = view.findViewById(R.id.btn_change_pass);
        logOut = view.findViewById(R.id.btn_log_out);


        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);
    }

    @Override
    public void changePassword(String oldPass, String newPass) {
        Map<String, String> map = new HashMap<>();

        map.put("old_password", oldPass);
        map.put("new_password", newPass);
        dialog.show();
        ws.getApi().changePassword(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        JSONObject res = new JSONObject(response.body().string());
                        Toast.makeText(getContext(), res.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), res.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void logout() {
        ws.getApi().logout(UserUtils.getAccessToken(getContext())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}