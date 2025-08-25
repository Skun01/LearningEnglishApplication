package com.example.learningenglishapplication.Vocabulary;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ChooseVocabularyTypeDialog extends DialogFragment {

    public interface OnVocabularyTypeSelectedListener {
        void onVocabularyTypeSelected(VocabularyType type);
    }

    public enum VocabularyType {
        ALL,
        UNLEARNED,
        REVIEW,
        FAVORITE
    }

    private OnVocabularyTypeSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnVocabularyTypeSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnVocabularyTypeSelectedListener");
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
        return inflater.inflate(R.layout.dialog_choose_vocabulary_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_choose_vocabulary);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        MaterialCardView cardAllVocabulary = view.findViewById(R.id.card_all_vocabulary);
        MaterialCardView cardUnlearnedVocabulary = view.findViewById(R.id.card_unlearned_vocabulary);
        MaterialCardView cardReviewVocabulary = view.findViewById(R.id.card_review_vocabulary);
        MaterialCardView cardFavoriteVocabulary = view.findViewById(R.id.card_favorite_vocabulary);

        cardAllVocabulary.setOnClickListener(v -> {
            listener.onVocabularyTypeSelected(VocabularyType.ALL);
            dismiss();
        });

        cardUnlearnedVocabulary.setOnClickListener(v -> {
            listener.onVocabularyTypeSelected(VocabularyType.UNLEARNED);
            dismiss();
        });

        cardReviewVocabulary.setOnClickListener(v -> {
            listener.onVocabularyTypeSelected(VocabularyType.REVIEW);
            dismiss();
        });

        cardFavoriteVocabulary.setOnClickListener(v -> {
            listener.onVocabularyTypeSelected(VocabularyType.FAVORITE);
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