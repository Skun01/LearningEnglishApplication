package com.example.learningenglishapplication.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Auth.LoginActivity;
import com.example.learningenglishapplication.Data.DataHelper.UserSettingDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ProfileSettingsActivity extends AppCompatActivity {

    private UserSettingDataHelper userSettingDataHelper;
    private long userId;
    private BarChart barChart;

    private LinearLayout layoutTheme;
    private TextView tvCurrentTheme;
    private LinearLayout layoutLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Khởi tạo helper
        userSettingDataHelper = new UserSettingDataHelper(this);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong("userId", -1);

        layoutTheme = findViewById(R.id.layout_theme_setting);
        tvCurrentTheme = findViewById(R.id.tv_theme);
        layoutLogout = findViewById(R.id.layout_logout);
        barChart = findViewById(R.id.bar_chart_stats);

        // Tải và hiển thị theme hiện tại
        String currentTheme = userSettingDataHelper.getThemeSetting(userId);
        tvCurrentTheme.setText(currentTheme);
        ThemeManager.applyTheme(currentTheme);

        // Xử lý đổi theme
        layoutTheme.setOnClickListener(v -> showThemeChooserDialog());

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
}
