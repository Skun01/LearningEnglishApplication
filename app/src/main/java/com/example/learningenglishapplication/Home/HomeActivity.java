package com.example.learningenglishapplication.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Achivements.AchievementsActivity;
import com.example.learningenglishapplication.Auth.LoginActivity;
import com.example.learningenglishapplication.Data.DataHelper.CategoryDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.UserDataHelper;
import com.example.learningenglishapplication.Profile.ProfileSettingsActivity;
import com.example.learningenglishapplication.Quiz.QuizSetupActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Vocabulary.VocabularyListActivity;
import com.example.learningenglishapplication.category.CategoryAdapter;
import com.example.learningenglishapplication.category.CategoryManagementActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private CategoryDataHelper categoryDataHelper;
    private UserDataHelper userDataHelper;
    private long currentUserId;
    private String currentUserEmail;
    private BottomNavigationView bottomNavigation;
    private TextView quoteTextView;
    private TextView tvLoiChao, tvTenNguoiDung;
    private ImageView ivAvatar;

    private final String[][] quotes = {
            {"Achieve", "Đạt được"},
            {"Attempt", "Nỗ lực, cố gắng"},
            {"Improve", "Cải thiện"},
            {"Challenge", "Thử thách"},
            {"Confident", "Tự tin"},
            {"Effort", "Nỗ lực"},
            {"Focus", "Tập trung"},
            {"Goal", "Mục tiêu"},
            {"Habit", "Thói quen"},
            {"Inspire", "Truyền cảm hứng"},
            {"Knowledge", "Kiến thức"},
            {"Learn", "Học"},
            {"Motivate", "Tạo động lực"},
            {"Opportunity", "Cơ hội"},
            {"Patience", "Sự kiên nhẫn"},
            {"Practice", "Luyện tập"},
            {"Progress", "Tiến bộ"},
            {"Success", "Thành công"},
            {"Try", "Thử"},
            {"Value", "Giá trị"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ view
        tvLoiChao = findViewById(R.id.tv_loi_chao);
        tvTenNguoiDung = findViewById(R.id.tv_ten_nguoi_dung);
        ivAvatar = findViewById(R.id.nut_ho_so);

        // Lấy thông tin đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);
        currentUserEmail = sharedPreferences.getString("userEmail", null);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn || currentUserId == -1 || currentUserEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Lấy nickname
        userDataHelper = new UserDataHelper(this);
        String nickname = userDataHelper.getNickname(currentUserEmail);

        if (nickname != null && !nickname.isEmpty()) {
            tvTenNguoiDung.setText(nickname);
        } else {
            tvTenNguoiDung.setText("Người dùng");
        }
        tvLoiChao.setText("Chào mừng quay trở lại ✌️,");

        // Xử lý click avatar → mở AchievementsActivity
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AchievementsActivity.class);
            startActivity(intent);
        });

        // RecyclerView
        recyclerView = findViewById(R.id.rv_home_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryDataHelper = new CategoryDataHelper(this);
        loadCategories();

        // Quotes
        quoteTextView = findViewById(R.id.quote_text);
        quoteTextView.setOnClickListener(v -> showRandomQuote());

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void showRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        String english = quotes[index][0];
        String vietnamese = quotes[index][1];
        quoteTextView.setText(english + "\n" + vietnamese);
    }

    private void loadCategories() {
        adapter = new CategoryAdapter(this, categoryDataHelper.getAllCategories(currentUserId));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(HomeActivity.this, CategoryManagementActivity.class));
                return true;
            } else if (itemId == R.id.nav_quiz) {
                startActivity(new Intent(HomeActivity.this, QuizSetupActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileSettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(long categoryId, String categoryName) {
        Intent intent = new Intent(this, VocabularyListActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        intent.putExtra("CATEGORY_NAME", categoryName);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(long categoryId, String categoryName) {
        // Không xử lý long click
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }
}
