package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.learningenglishapplication.category.CategoryManagementActivity;
import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;

import com.example.learningenglishapplication.Data.DataHelper.CategoryDataHelper; // Cập nhật import
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper; // Cập nhật import
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuizSetupActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etNumberOfQuestions;
    private RadioGroup rgQuizType;
    private RadioButton rbMultipleChoice;
    private RadioButton rbMeaningQuiz;
    private RadioButton rbMatching;
    private Button btnStartQuiz;
    private DatabaseHelper databaseHelper;
    private VocabularyDataHelper vocabularyDataHelper; // Sửa tên lớp
    private CategoryDataHelper categoryDataHelper; // Sửa tên lớp
    private BottomNavigationView bottomNavigation;

    private Map<String, Long> categoryMap;
    private List<String> categoryNames;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_setup);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);
        // Khởi tạo các DataHelper mới
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);
        categoryDataHelper = new CategoryDataHelper(this); // CategoryDataHelper cần Context

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        spinnerCategory = findViewById(R.id.spinner_quiz_category);
        etNumberOfQuestions = findViewById(R.id.et_number_of_questions);
        rgQuizType = findViewById(R.id.rg_quiz_type);
        rbMultipleChoice = findViewById(R.id.rb_multiple_choice);
        rbMeaningQuiz = findViewById(R.id.rb_meaning_quiz);
        rbMatching = findViewById(R.id.rb_matching);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);

        loadCategoriesIntoSpinner();
        setupBottomNavigation();

        btnStartQuiz.setOnClickListener(v -> startQuiz());
    }
    
    private void handleSelectedCategory() {
        // Kiểm tra xem có category được chọn từ màn hình trước không
        Intent intent = getIntent();
        if (intent.hasExtra("SELECTED_CATEGORY_ID") && intent.hasExtra("SELECTED_CATEGORY_NAME")) {
            long selectedCategoryId = intent.getLongExtra("SELECTED_CATEGORY_ID", -1);
            String selectedCategoryName = intent.getStringExtra("SELECTED_CATEGORY_NAME");
            
            if (selectedCategoryId != -1 && selectedCategoryName != null) {
                // Tìm vị trí của category trong spinner
                int position = categoryNames.indexOf(selectedCategoryName);
                if (position >= 0) {
                    // Chọn category trong spinner
                    spinnerCategory.setSelection(position);
                    // Cập nhật số lượng câu hỏi
                    updateQuestionCount();
                }
            }
        }
    }

    private void loadCategoriesIntoSpinner() {
        categoryMap = categoryDataHelper.getCategoriesForSpinner(userId); // Sử dụng CategoryDataHelper
        categoryNames = new ArrayList<>(categoryMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        
        // Thêm listener để cập nhật số lượng câu hỏi khi chọn thể loại
        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                updateQuestionCount();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Không làm gì
            }
        });
        
        // Cập nhật số lượng câu hỏi ban đầu nếu có thể loại
        if (!categoryNames.isEmpty()) {
            updateQuestionCount();
        }
        
        // Kiểm tra xem có category được chọn từ màn hình trước không
        handleSelectedCategory();
    }
    
    private void updateQuestionCount() {
        if (categoryNames.isEmpty()) return;
        
        String selectedCategoryName = spinnerCategory.getSelectedItem().toString();
        long selectedCategoryId = categoryMap.get(selectedCategoryName);
        
        // Lấy danh sách từ vựng trong thể loại
        List<Vocabulary> vocabsInCategory = vocabularyDataHelper.getVocabulariesAsList(selectedCategoryId);
        int vocabCount = vocabsInCategory.size();
        
        // Cập nhật số lượng câu hỏi bằng số lượng từ vựng
        etNumberOfQuestions.setText(String.valueOf(vocabCount));
        
        // Hiển thị thông báo nếu không đủ từ vựng
        if (vocabCount < 4) {
            Toast.makeText(this, "Thể loại này cần ít nhất 4 từ để tạo bài kiểm tra!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startQuiz() {
        if (categoryNames.isEmpty()) {
            Toast.makeText(this, "Bạn cần tạo thể loại trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCategoryName = spinnerCategory.getSelectedItem().toString();
        long selectedCategoryId = categoryMap.get(selectedCategoryName);
        String numQuestionsStr = etNumberOfQuestions.getText().toString().trim();

        if (TextUtils.isEmpty(numQuestionsStr)) {
            Toast.makeText(this, "Vui lòng nhập số câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfQuestions;
        try {
            numberOfQuestions = Integer.parseInt(numQuestionsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số câu hỏi không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numberOfQuestions <= 0) {
            Toast.makeText(this, "Số câu hỏi phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // SỬA ĐỔI: Lấy danh sách từ vựng thông qua VocabularyDataHelper
        List<Vocabulary> allVocabs = vocabularyDataHelper.getVocabulariesAsList(selectedCategoryId);

        if (allVocabs.size() < 4) {
            Toast.makeText(this, "Thể loại này cần ít nhất 4 từ để tạo bài kiểm tra!", Toast.LENGTH_LONG).show();
            return;
        }

        if (numberOfQuestions > allVocabs.size()) {
            Toast.makeText(this, "Số câu hỏi không thể lớn hơn số từ vựng hiện có (" + allVocabs.size() + ")", Toast.LENGTH_LONG).show();
            return;
        }

        // Trộn và chọn câu hỏi
        Collections.shuffle(allVocabs);
        List<Vocabulary> quizQuestions = new ArrayList<>(allVocabs.subList(0, numberOfQuestions));

        // Xác định loại quiz được chọn
        Intent intent;
        if (rbMatching.isChecked()) {
            intent = new Intent(this, MatchingQuizActivity.class);
        } else if (rbMeaningQuiz.isChecked()) {
            intent = new Intent(this, MeaningQuizActivity.class);
        } else {
            intent = new Intent(this, QuizActivity.class);
        }
        
        intent.putExtra("QUIZ_QUESTIONS", (Serializable) quizQuestions);
        intent.putExtra("ALL_VOCABS", (Serializable) allVocabs);
        intent.putExtra("USER_ID", userId);
        
        // Sử dụng ActivityTransitionManager để khởi chạy activity với hiệu ứng chuyển đổi
        ActivityTransitionManager.startActivityWithDefaultTransition(this, intent);
    }
    
    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_quiz);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                ActivityTransitionManager.startActivityWithSlideTransition(
                        this, HomeActivity.class);
                return true;
            } else if (itemId == R.id.nav_categories) {
                ActivityTransitionManager.startActivityWithSlideTransition(
                        this, CategoryManagementActivity.class);
                return true;
            } else if (itemId == R.id.nav_quiz) {
                // Đã ở màn hình Quiz Setup, không cần chuyển đổi
                return true;
            }
            return false;
        });
    }
}