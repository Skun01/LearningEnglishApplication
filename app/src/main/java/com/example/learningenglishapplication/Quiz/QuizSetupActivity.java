package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuizSetupActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etNumberOfQuestions;
    private Button btnStartQuiz;
    private DatabaseHelper databaseHelper;

    private Map<String, Long> categoryMap;
    private List<String> categoryNames;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_setup);

        databaseHelper = new DatabaseHelper(this);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        spinnerCategory = findViewById(R.id.spinner_quiz_category);
        etNumberOfQuestions = findViewById(R.id.et_number_of_questions);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);

        loadCategoriesIntoSpinner();

        btnStartQuiz.setOnClickListener(v -> startQuiz());
    }

    private void loadCategoriesIntoSpinner() {
        categoryMap = databaseHelper.getCategoriesForSpinner(userId);
        categoryNames = new ArrayList<>(categoryMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
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

        // Lấy danh sách từ vựng
        List<Vocabulary> allVocabs = databaseHelper.getVocabulariesAsList(selectedCategoryId);

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

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("QUIZ_QUESTIONS", (Serializable) quizQuestions);
        intent.putExtra("ALL_VOCABS", (Serializable) allVocabs);
        startActivity(intent);
    }
}
