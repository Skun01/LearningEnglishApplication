package com.example.learningenglishapplication.Achivements;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AchievementAdapter adapter;
    private List<Achievement> achievementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        recyclerView = findViewById(R.id.rv_achievements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo dữ liệu demo
        achievementList = new ArrayList<>();
        achievementList.add(new Achievement(R.drawable.ic_streak, "Chuỗi học tập", "Bạn đã học liên tục 7 ngày"));
        achievementList.add(new Achievement(R.drawable.ic_book, "Học từ mới", "Bạn đã học được 100 từ mới"));
        achievementList.add(new Achievement(R.drawable.ic_night, "Cú đêm", "Bạn đã học sau 23:00 nhiều lần"));
        achievementList.add(new Achievement(R.drawable.ic_sunrise, "Thợ săn bình minh", "Bạn đã học vào sáng sớm trước 6:00"));

        adapter = new AchievementAdapter(achievementList);
        recyclerView.setAdapter(adapter);
    }
}
