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
 * Activity cơ sở cung cấp các chức năng chung cho tất cả các activity trong ứng dụng
 * Bao gồm toolbar, nút quay lại, và các phương thức tiện ích
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected TextView tvTitle;
    protected ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Thiết lập toolbar với tiêu đề và nút quay lại
     * @param title Tiêu đề hiển thị trên toolbar
     * @param showBackButton true để hiển thị nút quay lại, false để ẩn
     */
    protected void setupToolbar(String title, boolean showBackButton) {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        btnBack = findViewById(R.id.btn_back);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            if (tvTitle != null) {
                tvTitle.setText(title);
            }

            if (btnBack != null) {
                if (showBackButton) {
                    btnBack.setVisibility(View.VISIBLE);
                    btnBack.setOnClickListener(v -> onBackPressed());
                } else {
                    btnBack.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Thiết lập toolbar với tiêu đề và hiển thị nút quay lại
     * @param title Tiêu đề hiển thị trên toolbar
     */
    protected void setupToolbar(String title) {
        setupToolbar(title, true);
    }

    /**
     * Cập nhật tiêu đề trên toolbar
     * @param title Tiêu đề mới
     */
    protected void setToolbarTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
}