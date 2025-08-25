package com.example.learningenglishapplication.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class DeleteConfirmationDialog extends DialogFragment {

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(long itemId, String itemName);
    }

    private OnDeleteConfirmedListener listener;
    private long itemId;
    private String itemName;
    private String itemType;

    public static DeleteConfirmationDialog newInstance(long itemId, String itemName, String itemType) {
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog();
        Bundle args = new Bundle();
        args.putLong("itemId", itemId);
        args.putString("itemName", itemName);
        args.putString("itemType", itemType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnDeleteConfirmedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDeleteConfirmedListener");
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
        return inflater.inflate(R.layout.dialog_delete_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            itemId = getArguments().getLong("itemId");
            itemName = getArguments().getString("itemName");
            itemType = getArguments().getString("itemType", "mục");
        }

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_delete_confirmation);
        TextView tvDeleteMessage = view.findViewById(R.id.tv_delete_message);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnDelete = view.findViewById(R.id.btn_delete);

        tvDeleteMessage.setText(String.format("Bạn có chắc chắn muốn xóa %s '%s'?", itemType, itemName));

        toolbar.setNavigationOnClickListener(v -> dismiss());

        btnCancel.setOnClickListener(v -> dismiss());

        btnDelete.setOnClickListener(v -> {
            listener.onDeleteConfirmed(itemId, itemName);
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