package com.example.followup.home.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.followup.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_choose_lang extends BottomSheetDialogFragment {


    String selectedLanguage;

    public BottomSheet_choose_lang(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_choose_lang, container, false);
    }

    RadioGroup radioGroup;
    ImageView closeButton;
    Button doneButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioGroup = view.findViewById(R.id.radioGroup);
        doneButton = view.findViewById(R.id.btn_done);
        closeButton = view.findViewById(R.id.img_close);

        if (selectedLanguage.equals("ar")) {
            radioGroup.check(R.id.radio_ar);
        } else {
            radioGroup.check(R.id.radio_en);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_en:
                    selectedLanguage = "en";
                    // do operations specific to this selection
                    break;
                case R.id.radio_ar:
                    selectedLanguage = "ar";
                    // do operations specific to this selection
                    break;
            }
        });

        closeButton.setOnClickListener(v -> dismiss());

        doneButton.setOnClickListener(v -> sendBackResult(selectedLanguage));

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface LanguageSelectedListener {
        void languageSelectedListener(String languageSelected);
    }

    public void sendBackResult(String languageSelected) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        LanguageSelectedListener listener = (LanguageSelectedListener) getTargetFragment();
        listener.languageSelectedListener(languageSelected);
        dismiss();
    }
}

