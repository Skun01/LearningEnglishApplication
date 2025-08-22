package com.example.learningenglishapplication.Utils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.learningenglishapplication.R;

/**
 * Activity cơ sở cho các activity con (không có bottom navigation)
 * Kế thừa từ BaseActivity và cung cấp các phương thức chung cho các activity con
 */
public abstract class BaseChildActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Thiết lập toolbar với tiêu đề và nút quay lại
     * Ghi đè phương thức từ BaseActivity để đảm bảo nút quay lại luôn được hiển thị
     * @param title Tiêu đề hiển thị trên toolbar
     */
    @Override
    protected void setupToolbar(String title) {
        super.setupToolbar(title, true);
    }

    /**
     * Ghi đè phương thức onBackPressed để sử dụng hiệu ứng chuyển tiếp mặc định
     */
    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
}