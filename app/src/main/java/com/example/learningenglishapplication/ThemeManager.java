package com.example.learningenglishapplication;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    public static final String LIGHT_MODE = "Light";
    public static final String DARK_MODE = "Dark";
    public static final String SYSTEM_MODE = "System";

    public static void applyTheme(String theme) {
        switch (theme) {
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}