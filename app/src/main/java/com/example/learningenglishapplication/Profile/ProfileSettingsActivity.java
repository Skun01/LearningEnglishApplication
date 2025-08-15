package com.example.learningenglishapplication.Profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;

import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
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

    private StatisticsDataHelper statisticsHelper;
    private UserSettingDataHelper userSettingHelper;
    private long userId;
    private BarChart barChart;

    private LinearLayout layoutTheme;
    private TextView tvCurrentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        statisticsHelper = new StatisticsDataHelper(this);
        userSettingHelper = new UserSettingDataHelper(this);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        layoutTheme = findViewById(R.id.layout_theme_setting); // Thêm ID này vào layout
        tvCurrentTheme = findViewById(R.id.tv_theme); // Dùng ID cũ

        // Tải và hiển thị cài đặt theme hiện tại
        String currentTheme = userSettingHelper.getThemeSetting(userId);
        tvCurrentTheme.setText(currentTheme);
        // Áp dụng theme ngay khi activity được tạo
        ThemeManager.applyTheme(currentTheme);

        layoutTheme.setOnClickListener(v -> showThemeChooserDialog());
        barChart = findViewById(R.id.bar_chart_stats);
        setupStatisticsChart();
    }

    private void setupStatisticsChart() {
        Cursor cursor = statisticsHelper.getWeeklyStats(userId);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;

        if (cursor.moveToFirst()) {
            do {
                int wordsLearned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_DATE));
                // Lấy 5 ký tự cuối (ví dụ: 07-28)
                String shortDate = date.substring(5);

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
        dataSet.setColor(Color.parseColor("#03DAC5")); // Màu colorAccent

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Tùy chỉnh biểu đồ
        barChart.getDescription().setEnabled(false); // Tắt mô tả
        barChart.getLegend().setEnabled(false); // Tắt chú thích
        barChart.setFitBars(true); // Làm cho các cột vừa với khung

        // Tùy chỉnh trục X (ngày)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.invalidate(); // Vẽ lại biểu đồ
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
                        userSettingHelper.saveThemeSetting(userId, selectedTheme);
                        // Cập nhật text hiển thị
                        tvCurrentTheme.setText(selectedTheme);
                        // Áp dụng theme ngay lập tức
                        ThemeManager.applyTheme(selectedTheme);
                    }
                })
                .show();
    }
}
