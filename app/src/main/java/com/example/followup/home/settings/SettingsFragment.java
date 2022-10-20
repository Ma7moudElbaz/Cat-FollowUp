package com.example.followup.home.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.followup.bottomsheets.BottomSheet_choose_lang;
import com.example.followup.home.HomeActivity;
import com.example.followup.R;

import java.util.Locale;

public class SettingsFragment extends Fragment implements BottomSheet_choose_lang.LanguageSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    TextView languageSelected;
    HomeActivity activity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        languageSelected = view.findViewById(R.id.language_selected);

        if (Locale.getDefault().toString().equals("ar")) {
            languageSelected.setText(requireActivity().getString(R.string.arabic));
        } else {
            languageSelected.setText(requireActivity().getString(R.string.english));
        }

        languageSelected.setOnClickListener(v -> showLanguageBottomSheet());

    }

    public void showLanguageBottomSheet() {
        BottomSheet_choose_lang langBottomSheet =
                new BottomSheet_choose_lang(Locale.getDefault().toString());
        langBottomSheet.setTargetFragment(this, 300);
        langBottomSheet.show(getFragmentManager(), "country_brand");
    }

    @Override
    public void languageSelectedListener(String languageSelected) {
        if (!(Locale.getDefault().toString().equals(languageSelected))) {
            activity.setProjects();
            activity.setLanguage(languageSelected);
        }


    }
}