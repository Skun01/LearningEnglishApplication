package com.example.learningenglishapplication.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.R;
import com.google.android.material.button.MaterialButton;

/**
 * Dialog xác nhận đăng xuất khỏi ứng dụng
 */
public class LogoutConfirmationDialog extends DialogFragment {

    private OnLogoutConfirmedListener listener;
    
    /**
     * Interface để xử lý sự kiện khi người dùng xác nhận đăng xuất
     */
    public interface OnLogoutConfirmedListener {
        void onLogoutConfirmed();
    }
    
    /**
     * Tạo instance mới của LogoutConfirmationDialog
     * 
     * @return Instance của LogoutConfirmationDialog
     */
    public static LogoutConfirmationDialog newInstance() {
        return new LogoutConfirmationDialog();
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutConfirmedListener) {
            listener = (OnLogoutConfirmedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnLogoutConfirmedListener");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_logout_confirmation, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
        
        // Xử lý sự kiện khi nhấn nút hủy
        btnCancel.setOnClickListener(v -> dismiss());
        
        // Xử lý sự kiện khi nhấn nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLogoutConfirmed();
            }
            dismiss();
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Thiết lập chiều rộng của dialog là 90% màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.9);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        }
    }
}