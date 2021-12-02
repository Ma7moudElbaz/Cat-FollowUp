package com.example.followup.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.akexorcist.localizationactivity.ui.LocalizationApplication;

import java.util.Locale;


public class App extends LocalizationApplication {

    @NonNull
    @Override
    public Locale getDefaultLanguage(@NonNull Context context) {
        return Locale.ENGLISH;
    }

}
