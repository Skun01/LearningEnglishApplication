package com.example.learningenglishapplication.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.learningenglishapplication.Auth.LoginActivity;
import com.example.learningenglishapplication.Data.DataHelper.UserDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.UserSettingDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Dialog.DailyGoalDialog;
import com.example.learningenglishapplication.Dialog.LogoutConfirmationDialog;
import com.example.learningenglishapplication.Profile.EditProfileDialog;
import com.example.learningenglishapplication.Profile.ThemeChooserDialog;
import com.example.learningenglishapplication.Utils.BaseChildActivity;
import com.example.learningenglishapplication.Utils.NotificationHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ProfileSettingsActivity extends BaseChildActivity implements ThemeChooserDialog.OnThemeSelectedListener, EditProfileDialog.OnProfileUpdatedListener, DailyGoalDialog.OnDailyGoalSelectedListener, LogoutConfirmationDialog.OnLogoutConfirmedListener {

    private UserSettingDataHelper userSettingDataHelper;
    private UserDataHelper userDataHelper;
    private long userId;
    private BarChart barChart;

    private LinearLayout layoutTheme;
    private TextView tvCurrentTheme;
    private LinearLayout layoutLogout;
    
    private TextView tvUserName;
    private TextView tvUserEmail;
    private Button btnEditProfile;
    private ImageView ivUserAvatar;
    
    // UI cho mục tiêu học tập và nhắc nhở
    private LinearLayout layoutDailyGoal;
    private TextView tvDailyGoal;
    private Switch switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        
        // Thiết lập toolbar và nút quay lại
        MaterialToolbar toolbar = findViewById(R.id.toolbar_profile_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cài đặt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Khởi tạo helper
        userSettingDataHelper = new UserSettingDataHelper(this);
        userDataHelper = new UserDataHelper(this);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong("userId", -1);

        // Khởi tạo các thành phần UI
        layoutTheme = findViewById(R.id.layout_theme_setting);
        tvCurrentTheme = findViewById(R.id.tv_theme);
        layoutLogout = findViewById(R.id.layout_logout);
        barChart = findViewById(R.id.bar_chart_stats);
        
        // Khởi tạo các thành phần UI cho thông tin người dùng
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        
        // Khởi tạo các thành phần UI cho mục tiêu học tập và nhắc nhở
        layoutDailyGoal = findViewById(R.id.layout_daily_goal);
        tvDailyGoal = findViewById(R.id.tv_daily_goal);
        switchNotifications = findViewById(R.id.switch_notifications);

        // Tải và hiển thị theme hiện tại
        String currentTheme = userSettingDataHelper.getThemeSetting(userId);
        tvCurrentTheme.setText(currentTheme);
        ThemeManager.applyTheme(currentTheme);

        // Tải và hiển thị thông tin người dùng
        loadUserInfo();
        
        // Tải và hiển thị mục tiêu học tập
        loadDailyGoalAndNotifications();

        // Xử lý đổi theme
        layoutTheme.setOnClickListener(v -> showThemeChooserDialog());

        // Xử lý nút chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        
        // Xử lý thay đổi mục tiêu học tập
        layoutDailyGoal.setOnClickListener(v -> showDailyGoalDialog());
        
        // Xử lý thay đổi trạng thái nhắc nhở
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSettingDataHelper.saveNotificationSetting(userId, isChecked);
            updateNotificationSchedule(isChecked);
        });

        // Setup thống kê
        setupStatisticsChart();

        // Xử lý nút đăng xuất
        layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void setupStatisticsChart() {
        Cursor cursor = userSettingDataHelper.getWeeklyStats(userId);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;

        if (cursor.moveToFirst()) {
            do {
                int wordsLearned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_DATE));
                String shortDate = date.substring(5); // cắt yyyy- chỉ còn MM-dd

                entries.add(new BarEntry(i, wordsLearned));
                labels.add(shortDate);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (entries.isEmpty()) {
            barChart.setNoDataText("Chưa có dữ liệu thống kê. Hãy học từ mới!");
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số từ đã học");
        dataSet.setColor(Color.parseColor("#03DAC5"));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.invalidate();
    }

    private void showThemeChooserDialog() {
        ThemeChooserDialog dialog = new ThemeChooserDialog();
        dialog.show(getSupportFragmentManager(), "ThemeChooserDialog");
    }
    
    @Override
    public void onThemeSelected(String theme) {
        userSettingDataHelper.saveThemeSetting(userId, theme);
        tvCurrentTheme.setText(theme);
        ThemeManager.applyTheme(theme);
    }

    private void showLogoutDialog() {
        LogoutConfirmationDialog dialog = LogoutConfirmationDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "LogoutConfirmationDialog");
    }
    
    @Override
    public void onLogoutConfirmed() {
        logoutUser();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ProfileSettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void loadUserInfo() {
        if (userId != -1) {
            Cursor cursor = userDataHelper.getUserById(userId);
            if (cursor != null && cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NICKNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL));
                
                tvUserName.setText(username);
                tvUserEmail.setText(email);
                
                cursor.close();
            }
        }
    }
    
    private void showEditProfileDialog() {
        // Sử dụng EditProfileDialog mới
        String username = tvUserName.getText().toString();
        String email = tvUserEmail.getText().toString();
        EditProfileDialog dialog = EditProfileDialog.newInstance(userId, username, email);
        dialog.show(getSupportFragmentManager(), "EditProfileDialog");
    }
    
    /**
     * Triển khai phương thức từ OnProfileUpdatedListener
     */
    @Override
    public void onProfileUpdated(String username, String email) {
        // Cập nhật UI
        tvUserName.setText(username);
        tvUserEmail.setText(email);
    }
    
    @Override
    public void onDailyGoalSelected(int goal) {
        // Lưu mục tiêu mới
        userSettingDataHelper.saveDailyGoal(userId, goal);
        
        // Cập nhật UI
        tvDailyGoal.setText(goal + " từ");
        Toast.makeText(this, "Đã cập nhật mục tiêu hàng ngày", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Tải và hiển thị mục tiêu học tập và trạng thái nhắc nhở
     */
    private void loadDailyGoalAndNotifications() {
        // Hiển thị mục tiêu học tập
        int dailyGoal = userSettingDataHelper.getDailyGoal(userId);
        tvDailyGoal.setText(dailyGoal + " từ");
        
        // Hiển thị trạng thái nhắc nhở
        boolean notificationsEnabled = userSettingDataHelper.isNotificationsEnabled(userId);
        switchNotifications.setChecked(notificationsEnabled);
    }
    
    /**
     * Hiển thị dialog để thay đổi mục tiêu học tập hằng ngày
     */
    private void showDailyGoalDialog() {
        // Lấy mục tiêu hiện tại
        int currentGoal = userSettingDataHelper.getDailyGoal(userId);
        
        // Hiển thị dialog chọn mục tiêu hàng ngày
        DailyGoalDialog dialog = DailyGoalDialog.newInstance(currentGoal);
        dialog.show(getSupportFragmentManager(), "DailyGoalDialog");
    }
    
    /**
     * Cập nhật lịch trình thông báo nhắc nhở
     */
    private void updateNotificationSchedule(boolean enabled) {
        // Lưu cài đặt thông báo
        userSettingDataHelper.saveNotificationSetting(userId, enabled);
        
        // Khởi tạo NotificationHelper
        NotificationHelper notificationHelper = new NotificationHelper(this);
        
        if (enabled) {
            // Lên lịch thông báo vào 20:00 mỗi ngày
            notificationHelper.scheduleNotification(userId, 20, 0);
            Toast.makeText(this, "Đã bật nhắc nhở học tập hàng ngày", Toast.LENGTH_SHORT).show();
        } else {
            // Hủy thông báo
            notificationHelper.cancelNotification();
            Toast.makeText(this, "Đã tắt nhắc nhở học tập hàng ngày", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
