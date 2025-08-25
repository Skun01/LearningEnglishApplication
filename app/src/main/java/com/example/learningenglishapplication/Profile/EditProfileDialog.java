package com.example.learningenglishapplication.Profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.Data.DataHelper.UserDataHelper;
import com.example.learningenglishapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileDialog extends DialogFragment {

    private TextInputEditText etUsername, etEmail;
    private MaterialButton btnSave;
    private UserDataHelper userDataHelper;
    private long userId;
    private String currentUsername, currentEmail;
    private OnProfileUpdatedListener listener;

    public interface OnProfileUpdatedListener {
        void onProfileUpdated(String username, String email);
    }

    public static EditProfileDialog newInstance(long userId, String username, String email) {
        EditProfileDialog dialog = new EditProfileDialog();
        Bundle args = new Bundle();
        args.putLong("USER_ID", userId);
        args.putString("USERNAME", username);
        args.putString("EMAIL", email);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnProfileUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " phải triển khai OnProfileUpdatedListener");
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
        return inflater.inflate(R.layout.dialog_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các thành phần UI
        userDataHelper = new UserDataHelper(requireContext());
        etUsername = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        btnSave = view.findViewById(R.id.btn_save_profile);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_edit_profile);

        // Lấy dữ liệu từ arguments
        if (getArguments() != null) {
            userId = getArguments().getLong("USER_ID", -1);
            currentUsername = getArguments().getString("USERNAME", "");
            currentEmail = getArguments().getString("EMAIL", "");
        }

        // Đặt giá trị hiện tại
        etUsername.setText(currentUsername);
        etEmail.setText(currentEmail);

        // Thiết lập sự kiện
        toolbar.setNavigationOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Thiết lập kích thước dialog rộng hơn
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            
            // Thêm hiệu ứng bo góc cho dialog
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        }
    }

    private void saveProfile() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin người dùng
        boolean success = userDataHelper.updateUserInfo(userId, newUsername, newEmail);
        if (success) {
            Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            listener.onProfileUpdated(newUsername, newEmail);
            dismiss();
        } else {
            Toast.makeText(requireContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}