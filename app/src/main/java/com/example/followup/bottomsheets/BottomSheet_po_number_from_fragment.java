package com.example.followup.bottomsheets;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_po_number_from_fragment extends BottomSheetDialogFragment {


    final String type;
    final int projectId;
    private final BottomSheet_po_number_from_fragment.PoNumberSubmitListener poNumberSubmitListener;

    public BottomSheet_po_number_from_fragment(Fragment fragment, int projectId, String type) {
        this.poNumberSubmitListener = ((BottomSheet_po_number_from_fragment.PoNumberSubmitListener) fragment);
        this.projectId = projectId;
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

    @SuppressLint("NonConstantResourceId")
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
                    po_number_text = "";
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
            if (po_number_text.equals("")) {
                po_number_text = reasonEt.getText().toString();
            }
            if (po_number_text.length() == 0) {
                reasonEt.setError("This is required field");
            } else {
                sendBackResult(po_number_text,projectId, type);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface PoNumberSubmitListener {
        void poNumberSubmitListener(String reason,int projectId, String type);
    }

    public void sendBackResult(String poNumber,int projectId, String type) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        poNumberSubmitListener.poNumberSubmitListener(poNumber,projectId, type);
        dismiss();
    }
}

