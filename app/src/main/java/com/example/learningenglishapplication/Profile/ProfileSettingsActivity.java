package com.example.learningenglishapplication.Profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;

import com.example.learningenglishapplication.Data.DAO.StatisticsDAO;
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

    private StatisticsDAO statisticsDAO;
    private long userId;
    private BarChart barChart;

    private TextView tvCurrentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        statisticsDAO = new StatisticsDAO(this);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        barChart = findViewById(R.id.bar_chart_stats);
        setupStatisticsChart();
    }

    private void setupStatisticsChart() {
        Cursor cursor = statisticsDAO.getWeeklyStats(userId);
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
}
