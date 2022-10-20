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
import androidx.fragment.app.Fragment;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_choose_filter_all_requests extends BottomSheetDialogFragment {

    private final BottomSheet_choose_filter_all_requests.FilterListener filterListener;

    int chipSelectedIndex;
    public BottomSheet_choose_filter_all_requests(int chipSelectedIndex, Fragment fragment) {
        this.chipSelectedIndex = chipSelectedIndex;
        this.filterListener = ((BottomSheet_choose_filter_all_requests.FilterListener) fragment);
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
    String[] chipsText = new String[]{"Need action","All", "Waiting procurement", "Waiting sales approval", "Sales reject", "Procurement reject", "Approved"};
    String[] chipsStatus = new String[]{"","0", "2", "4", "5", "3", "6"};

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
        applyButton.setOnClickListener(v -> sendBackResult(chipSelectedIndex,chipsStatus[chipSelectedIndex]));


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface FilterListener {
        void applyFilterListener(int selectedStatusIndex,String selectedStatus);
    }

    public void sendBackResult(int selectedStatusIndex,String selectedStatus) {
        filterListener.applyFilterListener(selectedStatusIndex,selectedStatus);
        dismiss();
    }
}

