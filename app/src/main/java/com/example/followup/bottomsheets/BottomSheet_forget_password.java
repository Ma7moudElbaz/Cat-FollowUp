package com.example.followup.bottomsheets;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_forget_password extends BottomSheetDialogFragment {


    private final ForgetPassListener forgetPassListener;

    public BottomSheet_forget_password(Activity activity) {
        this.forgetPassListener = ((ForgetPassListener) activity);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_forget_password, container, false);
    }

    ImageView closeButton;
    Button submit;
    EditText email;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.btn_submit);
        closeButton = view.findViewById(R.id.img_close);
        email = view.findViewById(R.id.password);

        closeButton.setOnClickListener(v -> dismiss());

        submit.setOnClickListener(v -> {
            if (validateFields()) {
                sendBackResult(email.getText().toString());
            }
        });
    }


    private boolean validateFields() {
        if (email.length() == 0) {
            email.setError("This is required field");
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

    public interface ForgetPassListener {
        void forgetPassword(String email);
    }

    public void sendBackResult(String email) {
        forgetPassListener.forgetPassword(email);
        dismiss();
    }
}

