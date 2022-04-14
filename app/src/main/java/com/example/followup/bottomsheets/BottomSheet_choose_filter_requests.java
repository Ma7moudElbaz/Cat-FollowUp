package com.example.followup.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_choose_filter_requests extends BottomSheetDialogFragment {


    int chipSelectedIndex;

    public BottomSheet_choose_filter_requests(int chipSelectedIndex) {
        this.chipSelectedIndex = chipSelectedIndex;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    ImageView closeButton;
    Button applyButton, resetButton;
    ChipCloud statusChip;
    String[] chipsText = new String[]{"All","Waiting supplier cost", "Waiting Nagat", "Nagat reject", "Waiting sales", "Sales reject", "Approved"};


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyButton = view.findViewById(R.id.btn_apply);
        resetButton = view.findViewById(R.id.btn_reset);
        closeButton = view.findViewById(R.id.img_close);
        statusChip = view.findViewById(R.id.status_chip);
        statusChip.addChips(chipsText);

        if (chipSelectedIndex != -1) {
            statusChip.setSelectedChip(chipSelectedIndex);
        }
        statusChip.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                chipSelectedIndex = index;
            }

            @Override
            public void chipDeselected(int index) {
                chipSelectedIndex = -1;
            }
        });


        closeButton.setOnClickListener(v -> dismiss());
        applyButton.setOnClickListener(v -> sendBackResult(chipSelectedIndex));
//        resetButton.setOnClickListener(v -> statusChip.setSelectedChip(chipSelectedIndex));


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface FilterListener {
        void applyFilterListener(int selectedStatusIndex);
    }

    public void sendBackResult(int selectedStatusIndex) {
        FilterListener listener = (FilterListener) getActivity();
        listener.applyFilterListener(selectedStatusIndex);
        dismiss();
    }
}

