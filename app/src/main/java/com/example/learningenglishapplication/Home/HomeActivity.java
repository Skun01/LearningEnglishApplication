package com.example.learningenglishapplication.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Achivements.AchievementsActivity;
import com.example.learningenglishapplication.Auth.LoginActivity;
import com.example.learningenglishapplication.Data.DataHelper.CategoryDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.UserDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.Profile.ProfileSettingsActivity;
import com.example.learningenglishapplication.Quiz.QuizSetupActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;
import com.example.learningenglishapplication.Vocabulary.VocabularyListActivity;
import com.example.learningenglishapplication.category.CategoryAdapter;
import com.example.learningenglishapplication.category.CategoryManagementActivity;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView rvDailyWords;
    private CategoryAdapter adapter;
    private CategoryDataHelper categoryDataHelper;
    private UserDataHelper userDataHelper;
    private StatisticsDataHelper statisticsDataHelper;
    private VocabularyDataHelper vocabularyDataHelper;
    private DatabaseHelper databaseHelper;
    private long currentUserId;
    private String currentUserEmail;
    private NavigationBarView bottomNavigation;
    private TextView quoteTextView;
    private TextView tvLoiChao, tvTenNguoiDung;
    private TextView tvStreakCount, tvWordsLearned, tvAccuracy;
    private TextView tvThongBaoChuoi;
    private Button btnStartLearning;
    private Button btnViewAllDailyWords;
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
        
        // Ánh xạ view thống kê học tập
        tvStreakCount = findViewById(R.id.tv_streak_count);
        tvWordsLearned = findViewById(R.id.tv_words_learned);
        tvAccuracy = findViewById(R.id.tv_accuracy);
        tvThongBaoChuoi = findViewById(R.id.thong_bao_chuoi);
        btnStartLearning = findViewById(R.id.btn_start_learning);
        
        // Ánh xạ view từ vựng hàng ngày
        quoteTextView = findViewById(R.id.quote_text);
        rvDailyWords = findViewById(R.id.rv_daily_words);
        btnViewAllDailyWords = findViewById(R.id.btn_view_all_daily_words);

        // Khởi tạo các DataHelper
        databaseHelper = new DatabaseHelper(this);
        userDataHelper = new UserDataHelper(this);
        categoryDataHelper = new CategoryDataHelper(this);
        statisticsDataHelper = new StatisticsDataHelper(this);
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);
        
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
        String nickname = userDataHelper.getNickname(currentUserEmail);

        if (nickname != null && !nickname.isEmpty()) {
            tvTenNguoiDung.setText(nickname);
        } else {
            tvTenNguoiDung.setText("Người dùng");
        }
        tvLoiChao.setText("Chào mừng quay trở lại ✌️,");
        
        // Cập nhật thống kê học tập
        updateLearningStatistics();
        
        // Cập nhật từ vựng hàng ngày
        setupDailyVocabulary();

        // Xử lý click avatar → mở ProfileSettingsActivity
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
            ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_FADE);
        });

        // RecyclerView Categories
        recyclerView = findViewById(R.id.rv_home_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();
        
        // Xử lý sự kiện nút bắt đầu học
        btnStartLearning.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QuizSetupActivity.class);
            ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_SLIDE);
        });

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
                ActivityTransitionManager.startActivityWithSlideTransition(
                        this, CategoryManagementActivity.class);
                return true;
            } else if (itemId == R.id.nav_quiz) {
                ActivityTransitionManager.startActivityWithSlideTransition(
                        this, QuizSetupActivity.class);
                return true;
            // Đã xóa xử lý cho tab cá nhân và tích hợp vào icon ở trang chủ
            }
            return false;
        });
    }

    @Override
    public void onItemClick(long categoryId, String categoryName) {
        Intent intent = new Intent(this, VocabularyListActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        intent.putExtra("CATEGORY_NAME", categoryName);
        ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_ZOOM);
    }

    @Override
    public void onItemLongClick(long categoryId, String categoryName) {
        // Không xử lý long click
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
        updateLearningStatistics();
        setupDailyVocabulary();
    }
    
    /**
     * Cập nhật thống kê học tập từ cơ sở dữ liệu
     */
    private void updateLearningStatistics() {
        // Lấy dữ liệu thống kê từ cơ sở dữ liệu
        Cursor cursor = statisticsDataHelper.getWeeklyStats(currentUserId);
        
        int totalWordsLearned = 0;
        int streakDays = 0;
        int correctAnswers = 0;
        int totalAnswers = 0;
        
        // Tính toán số từ đã học và chuỗi ngày học
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int wordsLearned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED));
                totalWordsLearned += wordsLearned;
                
                if (wordsLearned > 0) {
                    streakDays++;
                }
            }
            cursor.close();
        }
        
        // Lấy số câu trả lời đúng và tổng số câu trả lời từ SharedPreferences
        SharedPreferences quizStats = getSharedPreferences("quiz_stats", MODE_PRIVATE);
        correctAnswers = quizStats.getInt("correct_answers", 0);
        totalAnswers = quizStats.getInt("total_answers", 0);
        
        // Cập nhật UI
        tvStreakCount.setText(String.valueOf(streakDays));
        tvWordsLearned.setText(String.valueOf(totalWordsLearned));
        
        // Tính và hiển thị độ chính xác
        int accuracy = totalAnswers > 0 ? (correctAnswers * 100 / totalAnswers) : 0;
        tvAccuracy.setText(accuracy + "%");
        
        // Hiển thị thông báo chuỗi phù hợp
        if (streakDays > 0) {
            tvThongBaoChuoi.setText("Bạn đã học liên tục " + streakDays + " ngày. Hãy tiếp tục phát huy!");
        } else {
            tvThongBaoChuoi.setText("Bạn chưa có chuỗi nào, hãy bắt đầu bài học ngay nào!");
        }
    }
    
    /**
     * Thiết lập phần từ vựng hàng ngày
     */
    private void setupDailyVocabulary() {
        // Lấy danh sách từ vựng ngẫu nhiên từ tất cả các danh mục
        List<Vocabulary> allVocabulary = new ArrayList<Vocabulary>();
        List<Long> categories = categoryDataHelper.getAllCategoryIds(currentUserId);
        
        for (Long categoryId : categories) {
            allVocabulary.addAll(vocabularyDataHelper.getVocabulariesAsList(categoryId));
        }
        
        // Nếu không có từ vựng nào, hiển thị thông báo
        if (allVocabulary.isEmpty()) {
            rvDailyWords.setVisibility(View.GONE);
            btnViewAllDailyWords.setVisibility(View.GONE);
            quoteTextView.setText("Bạn chưa có từ vựng nào. Hãy thêm từ vựng để xem gợi ý hàng ngày!");
            return;
        }
        
        // Hiển thị từ vựng ngẫu nhiên
        showRandomQuote();
        
        // TODO: Thêm adapter cho RecyclerView từ vựng hàng ngày
        // DailyWordsAdapter dailyWordsAdapter = new DailyWordsAdapter(this, dailyWords);
        // rvDailyWords.setAdapter(dailyWordsAdapter);
        // rvDailyWords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        // Tạm thời ẩn RecyclerView từ vựng hàng ngày
        rvDailyWords.setVisibility(View.GONE);
        
        // Xử lý sự kiện nút xem tất cả
        btnViewAllDailyWords.setOnClickListener(v -> {
            // TODO: Mở màn hình xem tất cả từ vựng gợi ý
            Toast.makeText(HomeActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}
