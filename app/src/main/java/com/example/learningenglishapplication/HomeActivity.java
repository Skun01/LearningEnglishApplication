package com.example.learningenglishapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    // Khai báo các View
    private TextView tvWelcomeUser;
    private CardView cardCategories, cardCreateQuiz, cardSearch, cardStatistics;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Kiểm tra đăng nhập và lấy thông tin người dùng ---
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Nếu chưa đăng nhập, quay về màn hình Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return; // Dừng thực thi code bên dưới
        }
        // Lấy email để hiển thị lời chào
        String userEmail = sharedPreferences.getString("userEmail", "Người dùng");


        // --- Ánh xạ View từ layout XML ---
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        cardCategories = findViewById(R.id.card_categories);
        cardCreateQuiz = findViewById(R.id.card_create_quiz);
        cardSearch = findViewById(R.id.card_search);
        cardStatistics = findViewById(R.id.card_statistics);
        bottomNavigation = findViewById(R.id.bottom_navigation);


        // --- Cá nhân hóa lời chào ---
        tvWelcomeUser.setText("Xin chào, " + userEmail + "!");


        // --- Xử lý sự kiện cho các thẻ chức năng (CardView) ---
        cardCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình quản lý thể loại
                startActivity(new Intent(HomeActivity.this, CategoryManagementActivity.class));
            }
        });

        // (Để dành) Xử lý cho các thẻ khác
        cardCreateQuiz.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, QuizSetupActivity.class));
        });

        cardSearch.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Chức năng Tìm kiếm sẽ được phát triển!", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(HomeActivity.this, SearchExportActivity.class));
        });

        cardStatistics.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Chức năng Thống kê sẽ được phát triển!", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(HomeActivity.this, ProfileSettingsActivity.class));
        });


        // --- Xử lý sự kiện cho thanh điều hướng dưới cùng (BottomNavigationView) ---
        // Đặt mục "Trang chủ" được chọn mặc định
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Đang ở trang chủ rồi nên không làm gì cả
                    return true;
                } else if (itemId == R.id.nav_categories) {
                    // Chuyển đến trang Thể loại
                    startActivity(new Intent(getApplicationContext(), CategoryManagementActivity.class));
                    // overridePendingTransition(0, 0); // Hiệu ứng chuyển cảnh (tùy chọn)
                    return true;
                } else if (itemId == R.id.nav_quiz) {
                    // Chuyển đến trang Kiểm tra
                     startActivity(new Intent(getApplicationContext(), QuizSetupActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                     startActivity(new Intent(getApplicationContext(), ProfileSettingsActivity.class));
                    return true;
                }
                return false;
            }
        });


    }
}