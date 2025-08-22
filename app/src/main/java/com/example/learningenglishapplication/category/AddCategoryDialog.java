package com.example.learningenglishapplication.category;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.Data.DataHelper.CategoryDataHelper;
import com.example.learningenglishapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import static android.content.Context.MODE_PRIVATE;

public class AddCategoryDialog extends DialogFragment {

    private TextInputEditText etName, etDescription;
    private MaterialButton btnSave;
    private CategoryDataHelper categoryHelper;
    private long currentUserId;
    private OnCategorySavedListener listener;

    public interface OnCategorySavedListener {
        void onCategorySaved();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnCategorySavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " phải triển khai OnCategorySavedListener");
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
        return inflater.inflate(R.layout.dialog_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryHelper = new CategoryDataHelper(requireContext());
        etName = view.findViewById(R.id.et_category_name);
        etDescription = view.findViewById(R.id.et_category_description);
        btnSave = view.findViewById(R.id.btn_save_category);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_add_category);

        // Lấy User ID
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveCategory());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    private void saveCategory() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Tên thể loại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdded = categoryHelper.addCategory(name, description, currentUserId);
        if (isAdded) {
            Toast.makeText(requireContext(), "Đã lưu thể loại!", Toast.LENGTH_SHORT).show();
            listener.onCategorySaved();
            dismiss();
        } else {
            Toast.makeText(requireContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}