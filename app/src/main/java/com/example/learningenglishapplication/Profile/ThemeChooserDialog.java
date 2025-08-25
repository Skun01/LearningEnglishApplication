package com.example.learningenglishapplication.Profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Profile.ThemeManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ThemeChooserDialog extends DialogFragment {

    public interface OnThemeSelectedListener {
        void onThemeSelected(String theme);
    }

    private OnThemeSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnThemeSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnThemeSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_theme_chooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_theme_chooser);
        MaterialCardView cardLightTheme = view.findViewById(R.id.card_light_theme);
        MaterialCardView cardDarkTheme = view.findViewById(R.id.card_dark_theme);
        MaterialCardView cardSystemTheme = view.findViewById(R.id.card_system_theme);

        toolbar.setNavigationOnClickListener(v -> dismiss());

        cardLightTheme.setOnClickListener(v -> {
            listener.onThemeSelected(ThemeManager.LIGHT_MODE);
            dismiss();
        });

        cardDarkTheme.setOnClickListener(v -> {
            listener.onThemeSelected(ThemeManager.DARK_MODE);
            dismiss();
        });

        cardSystemTheme.setOnClickListener(v -> {
            listener.onThemeSelected(ThemeManager.SYSTEM_MODE);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
                window.setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            }
        }
    }
}