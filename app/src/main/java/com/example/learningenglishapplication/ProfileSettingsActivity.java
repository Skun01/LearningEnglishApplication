package com.example.learningenglishapplication;

import android.app.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private long userId;

    private LinearLayout layoutTheme;
    private TextView tvCurrentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        databaseHelper = new DatabaseHelper(this);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        layoutTheme = findViewById(R.id.layout_theme_setting); // Thêm ID này vào layout
        tvCurrentTheme = findViewById(R.id.tv_theme); // Dùng ID cũ

        // Tải và hiển thị cài đặt theme hiện tại
        String currentTheme = databaseHelper.getThemeSetting(userId);
        tvCurrentTheme.setText(currentTheme);
        // Áp dụng theme ngay khi activity được tạo
        ThemeManager.applyTheme(currentTheme);

        layoutTheme.setOnClickListener(v -> showThemeChooserDialog());
    }

    private void showThemeChooserDialog() {
        final String[] themes = {ThemeManager.LIGHT_MODE, ThemeManager.DARK_MODE, ThemeManager.SYSTEM_MODE};

        new AlertDialog.Builder(this)
                .setTitle("Chọn Giao Diện")
                .setItems(themes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedTheme = themes[which];
                        // Lưu cài đặt mới vào DB
                        databaseHelper.saveThemeSetting(userId, selectedTheme);
                        // Cập nhật text hiển thị
                        tvCurrentTheme.setText(selectedTheme);
                        // Áp dụng theme ngay lập tức
                        ThemeManager.applyTheme(selectedTheme);
                    }
                })
                .show();
    }
}
