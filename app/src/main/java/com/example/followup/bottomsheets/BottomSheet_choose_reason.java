package com.example.followup.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_choose_reason extends BottomSheetDialogFragment {


   final String title,subtitle,reason,type;

    public BottomSheet_choose_reason(String title, String subtitle, String reason, String type) {
        this.title = title;
        this.subtitle = subtitle;
        this.reason = reason;
        this.type = type;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_reason, container, false);
    }

    TextView titleTv,subTitleTv;
    EditText reasonEt;
    ImageView closeButton;
    Button submitButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTv = view.findViewById(R.id.title);
        subTitleTv = view.findViewById(R.id.subTitle);
        reasonEt = view.findViewById(R.id.reason);
        submitButton = view.findViewById(R.id.btn_submit);
        closeButton = view.findViewById(R.id.img_close);

        titleTv.setText(title);
        subTitleTv.setText(subtitle);


        closeButton.setOnClickListener(v -> dismiss());
        submitButton.setOnClickListener(v -> {
            if (reasonEt.length()==0){
                reasonEt.setError("This is required field");
            }else {
                sendBackResult(reasonEt.getText().toString(),type);
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ReasonSubmitListener {
        void reasonSubmitListener(String reason,String type);
    }

    public void sendBackResult(String reason,String type) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        ReasonSubmitListener listener = (ReasonSubmitListener) getActivity();
        listener.reasonSubmitListener(reason,type);
        dismiss();
    }
}

