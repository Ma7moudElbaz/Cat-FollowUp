package com.example.followup.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_choose_change_password extends BottomSheetDialogFragment {


    private BottomSheet_choose_change_password.ChangePassListener changePassListener;

    public BottomSheet_choose_change_password(Fragment fragment) {
        this.changePassListener = ((BottomSheet_choose_change_password.ChangePassListener) fragment);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_change_password, container, false);
    }

    ImageView closeButton;
    Button submit;
    EditText password, new_password, confirm_new_password;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.btn_submit);
        closeButton = view.findViewById(R.id.img_close);
        password = view.findViewById(R.id.password);
        new_password = view.findViewById(R.id.new_pass);
        confirm_new_password = view.findViewById(R.id.confirm_new_pass);

        closeButton.setOnClickListener(v -> dismiss());

        submit.setOnClickListener(v -> {
            if (validateFields()) {
                if (new_password.getText().toString().equals(confirm_new_password.getText().toString())) {
                    sendBackResult(password.getText().toString(), new_password.getText().toString());
                } else {
                    Toast.makeText(getContext(), "new pass and confirm new pass not matched", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean validateFields() {
        if (password.length() == 0) {
            password.setError("This is required field");
            return false;
        }
        if (new_password.length() == 0) {
            new_password.setError("This is required field");
            return false;
        }
        if (confirm_new_password.length() == 0) {
            confirm_new_password.setError("This is required field");
            return false;
        }
        return true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ChangePassListener {
        void changePassword(String oldPass, String newPass);
    }

    public void sendBackResult(String oldPass, String newPass) {
        changePassListener.changePassword(oldPass, newPass);
        dismiss();
    }
}

