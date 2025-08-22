package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;
import com.example.learningenglishapplication.Utils.BaseChildActivity;

public class QuizResultActivity extends BaseChildActivity {

    private TextView tvFinalScore, tvCorrectAnswers, tvWrongAnswers;
    private Button btnRetryQuiz, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        
        // Thiết lập toolbar với tiêu đề "Kết quả"
        setupToolbar(getString(R.string.quiz_result_title));
        
        // Thiết lập shared element transition
        postponeEnterTransition();
        TextView scoreView = findViewById(R.id.tv_final_score);
        scoreView.setTransitionName("score_transition");
        startPostponedEnterTransition();

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        tvFinalScore = findViewById(R.id.tv_final_score);
        tvCorrectAnswers = findViewById(R.id.tv_correct_answers);
        tvWrongAnswers = findViewById(R.id.tv_wrong_answers);
        TextView tvAccuracy = findViewById(R.id.tv_accuracy);
        btnRetryQuiz = findViewById(R.id.btn_retry_quiz);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        // Hiển thị điểm số
        tvFinalScore.setText(String.format("%d/%d", score, totalQuestions));
        
        // Hiển thị số câu trả lời đúng và sai
        tvCorrectAnswers.setText(String.valueOf(score));
        tvWrongAnswers.setText(String.valueOf(totalQuestions - score));
        
        // Tính và hiển thị tỷ lệ chính xác
        int accuracyPercentage = totalQuestions > 0 ? (score * 100) / totalQuestions : 0;
        tvAccuracy.setText(String.format("%d%%", accuracyPercentage));

        btnRetryQuiz.setOnClickListener(v -> {
            // Quay lại màn hình thiết lập Quiz
            ActivityTransitionManager.finishWithDefaultTransition(this);
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // Xóa hết các activity cũ trên stack và tạo HomeActivity mới
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_FADE);
            ActivityTransitionManager.finishWithDefaultTransition(this);
        });
    }
    
    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
}
