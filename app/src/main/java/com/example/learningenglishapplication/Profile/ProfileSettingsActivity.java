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
import android.widget.EditText;
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
import com.example.learningenglishapplication.Utils.BaseChildActivity;
import com.example.learningenglishapplication.Utils.NotificationHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ProfileSettingsActivity extends BaseChildActivity {

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
        final String[] themes = {
                ThemeManager.LIGHT_MODE,
                ThemeManager.DARK_MODE,
                ThemeManager.SYSTEM_MODE
        };

        new AlertDialog.Builder(this)
                .setTitle("Chọn Giao Diện")
                .setItems(themes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedTheme = themes[which];
                        userSettingDataHelper.saveThemeSetting(userId, selectedTheme);
                        tvCurrentTheme.setText(selectedTheme);
                        ThemeManager.applyTheme(selectedTheme);
                    }
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> logoutUser())
                .setNegativeButton("Không", null)
                .show();
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
        // Tạo view cho dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        
        // Lấy các thành phần UI từ dialog
        EditText etUsername = dialogView.findViewById(R.id.et_username);
        EditText etEmail = dialogView.findViewById(R.id.et_email);
        
        // Đặt giá trị hiện tại
        etUsername.setText(tvUserName.getText());
        etEmail.setText(tvUserEmail.getText());
        
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin")
               .setView(dialogView)
               .setPositiveButton("Lưu", (dialog, which) -> {
                   // Lấy thông tin mới
                   String newUsername = etUsername.getText().toString().trim();
                   String newEmail = etEmail.getText().toString().trim();
                   
                   // Kiểm tra dữ liệu
                   if (newUsername.isEmpty() || newEmail.isEmpty()) {
                       Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   
                   // Cập nhật thông tin người dùng
                   boolean success = userDataHelper.updateUserInfo(userId, newUsername, newEmail);
                   if (success) {
                       // Cập nhật UI
                       tvUserName.setText(newUsername);
                       tvUserEmail.setText(newEmail);
                       Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Hủy", null);
        
        builder.create().show();
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
        // Tạo các lựa chọn mục tiêu
        final String[] options = {"5 từ", "10 từ", "15 từ", "20 từ", "25 từ", "30 từ"};
        final int[] values = {5, 10, 15, 20, 25, 30};
        
        // Lấy mục tiêu hiện tại
        int currentGoal = userSettingDataHelper.getDailyGoal(userId);
        int selectedIndex = 1; // Mặc định chọn 10 từ
        
        // Tìm vị trí của mục tiêu hiện tại trong mảng
        for (int i = 0; i < values.length; i++) {
            if (values[i] == currentGoal) {
                selectedIndex = i;
                break;
            }
        }
        
        // Tạo dialog
        new AlertDialog.Builder(this)
                .setTitle("Chọn mục tiêu hàng ngày")
                .setSingleChoiceItems(options, selectedIndex, null)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Lấy vị trí được chọn
                    int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    if (position != -1) {
                        // Lưu mục tiêu mới
                        int newGoal = values[position];
                        userSettingDataHelper.saveDailyGoal(userId, newGoal);
                        
                        // Cập nhật UI
                        tvDailyGoal.setText(newGoal + " từ");
                        Toast.makeText(this, "Đã cập nhật mục tiêu hàng ngày", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
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
}
