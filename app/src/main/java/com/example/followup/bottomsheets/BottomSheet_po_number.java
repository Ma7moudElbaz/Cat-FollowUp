package com.example.followup.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_po_number extends BottomSheetDialogFragment {


    final String type;

    public BottomSheet_po_number(String type) {
        this.type = type;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_po_number, container, false);
    }

    EditText reasonEt;
    ImageView closeButton;
    Button submitButton;
    RadioGroup po_type;
    String po_number_text = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reasonEt = view.findViewById(R.id.reason);
        submitButton = view.findViewById(R.id.btn_submit);
        closeButton = view.findViewById(R.id.img_close);
        po_type = view.findViewById(R.id.po_type);

        po_type.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_po_number:
                    reasonEt.setVisibility(View.VISIBLE);
                    po_number_text = reasonEt.getText().toString();
                    break;
                case R.id.radio_quotation_approved:
                    reasonEt.setVisibility(View.GONE);
                    po_number_text = "Quotation Approved";
                    break;
                case R.id.radio_sample:
                    reasonEt.setVisibility(View.GONE);
                    po_number_text = "Sample Request";
                    break;
            }
        });

        closeButton.setOnClickListener(v -> dismiss());
        submitButton.setOnClickListener(v -> {
            if (po_number_text.equals("")){
                po_number_text = reasonEt.getText().toString();
            }
            if (po_number_text.length() == 0) {
                reasonEt.setError("This is required field");
            } else {
                sendBackResult(po_number_text, type);
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

    public interface PoNumberSubmitListener {
        void poNumberSubmitListener(String reason, String type);
    }

    public void sendBackResult(String poNumber, String type) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        PoNumberSubmitListener listener = (PoNumberSubmitListener) getActivity();
        listener.poNumberSubmitListener(poNumber, type);
        dismiss();
    }
}

