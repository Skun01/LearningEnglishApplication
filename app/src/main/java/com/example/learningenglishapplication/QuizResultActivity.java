package com.example.learningenglishapplication;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvFinalScore, tvCorrectAnswers, tvWrongAnswers;
    private Button btnRetryQuiz, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        tvFinalScore = findViewById(R.id.tv_final_score);
        tvCorrectAnswers = findViewById(R.id.tv_correct_answers);
        tvWrongAnswers = findViewById(R.id.tv_wrong_answers);
        btnRetryQuiz = findViewById(R.id.btn_retry_quiz);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        tvFinalScore.setText(score + "/" + totalQuestions);
        tvCorrectAnswers.setText(String.valueOf(score));
        tvWrongAnswers.setText(String.valueOf(totalQuestions - score));

        btnRetryQuiz.setOnClickListener(v -> {
            // Quay lại màn hình thiết lập Quiz
            finish(); // Chỉ cần đóng màn hình kết quả
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // Xóa hết các activity cũ trên stack và tạo HomeActivity mới
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
