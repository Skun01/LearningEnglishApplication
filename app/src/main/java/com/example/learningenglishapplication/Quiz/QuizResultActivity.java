package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvFinalScore, tvCorrectAnswers, tvWrongAnswers;
    private Button btnRetryQuiz, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        int completionTime = getIntent().getIntExtra("COMPLETION_TIME", 0);

        tvFinalScore = findViewById(R.id.tv_final_score);
        tvCorrectAnswers = findViewById(R.id.tv_correct_answers);
        tvWrongAnswers = findViewById(R.id.tv_wrong_answers);
        btnRetryQuiz = findViewById(R.id.btn_retry_quiz);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        // Hiển thị thời gian hoàn thành thay vì điểm số
        int minutes = completionTime / 60;
        int seconds = completionTime % 60;
        tvFinalScore.setText(String.format("%d:%02d", minutes, seconds));
        
        // Vẫn hiển thị số cặp đúng và số cặp sai
        tvCorrectAnswers.setText(String.valueOf(score / 10)); // Mỗi cặp đúng được 10 điểm
        tvWrongAnswers.setText(String.valueOf(totalQuestions - (score / 10)));

        btnRetryQuiz.setOnClickListener(v -> {
            // Quay lại màn hình thiết lập Quiz
            ActivityTransitionManager.finishWithTransition(this, ActivityTransitionManager.TRANSITION_SLIDE);
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // Xóa hết các activity cũ trên stack và tạo HomeActivity mới
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_FADE);
            ActivityTransitionManager.finishWithTransition(this, ActivityTransitionManager.TRANSITION_FADE);
        });
    }
    
    @Override
     public void onBackPressed() {
         ActivityTransitionManager.finishWithTransition(this, ActivityTransitionManager.TRANSITION_SLIDE);
     }
}
