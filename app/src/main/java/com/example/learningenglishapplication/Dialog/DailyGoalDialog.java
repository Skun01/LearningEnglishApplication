package com.example.learningenglishapplication.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * Dialog cho phép người dùng chọn mục tiêu học từ vựng hàng ngày
 */
public class DailyGoalDialog extends DialogFragment {

    private static final String ARG_CURRENT_GOAL = "current_goal";
    
    private int currentGoal;
    private OnDailyGoalSelectedListener listener;
    
    /**
     * Interface để xử lý sự kiện khi người dùng chọn mục tiêu hàng ngày
     */
    public interface OnDailyGoalSelectedListener {
        void onDailyGoalSelected(int goal);
    }
    
    /**
     * Tạo instance mới của DailyGoalDialog
     * 
     * @param currentGoal Mục tiêu hiện tại của người dùng
     * @return Instance của DailyGoalDialog
     */
    public static DailyGoalDialog newInstance(int currentGoal) {
        DailyGoalDialog dialog = new DailyGoalDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_GOAL, currentGoal);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentGoal = getArguments().getInt(ARG_CURRENT_GOAL, 10); // Mặc định là 10 từ
        }
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDailyGoalSelectedListener) {
            listener = (OnDailyGoalSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnDailyGoalSelectedListener");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_daily_goal, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_daily_goal);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_daily_goal);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btn_confirm);
        
        // Thiết lập radio button tương ứng với mục tiêu hiện tại
        setSelectedRadioButton(radioGroup, currentGoal);
        
        // Xử lý sự kiện khi nhấn nút hủy
        btnCancel.setOnClickListener(v -> dismiss());
        
        // Xử lý sự kiện khi nhấn nút xác nhận
        btnConfirm.setOnClickListener(v -> {
            int selectedGoal = getSelectedGoal(radioGroup);
            if (listener != null) {
                listener.onDailyGoalSelected(selectedGoal);
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
    
    /**
     * Thiết lập radio button được chọn dựa trên mục tiêu hiện tại
     * 
     * @param radioGroup RadioGroup chứa các lựa chọn
     * @param goal Mục tiêu hiện tại
     */
    private void setSelectedRadioButton(RadioGroup radioGroup, int goal) {
        int radioButtonId;
        switch (goal) {
            case 5:
                radioButtonId = R.id.radio_5_words;
                break;
            case 10:
                radioButtonId = R.id.radio_10_words;
                break;
            case 15:
                radioButtonId = R.id.radio_15_words;
                break;
            case 20:
                radioButtonId = R.id.radio_20_words;
                break;
            case 25:
                radioButtonId = R.id.radio_25_words;
                break;
            case 30:
                radioButtonId = R.id.radio_30_words;
                break;
            default:
                radioButtonId = R.id.radio_10_words; // Mặc định là 10 từ
                break;
        }
        radioGroup.check(radioButtonId);
    }
    
    /**
     * Lấy mục tiêu được chọn từ RadioGroup
     * 
     * @param radioGroup RadioGroup chứa các lựa chọn
     * @return Mục tiêu được chọn
     */
    private int getSelectedGoal(RadioGroup radioGroup) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_5_words) {
            return 5;
        } else if (checkedId == R.id.radio_10_words) {
            return 10;
        } else if (checkedId == R.id.radio_15_words) {
            return 15;
        } else if (checkedId == R.id.radio_20_words) {
            return 20;
        } else if (checkedId == R.id.radio_25_words) {
            return 25;
        } else if (checkedId == R.id.radio_30_words) {
            return 30;
        } else {
            return 10; // Mặc định là 10 từ
        }
    }
}