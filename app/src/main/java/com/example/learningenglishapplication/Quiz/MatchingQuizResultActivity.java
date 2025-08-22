package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;
import com.example.learningenglishapplication.Utils.BaseActivity;

public class MatchingQuizResultActivity extends BaseActivity {

    private TextView tvCompletionTime, tvCorrectPairs, tvWrongPairs, tvTotalScore;
    private Button btnRetryQuiz, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_quiz_result);
        
        // Thiết lập toolbar với tiêu đề "Kết quả ghép từ"
        setupToolbar(getString(R.string.matching_quiz_result_title));
        
        // Thiết lập shared element transition
        postponeEnterTransition();
        TextView scoreView = findViewById(R.id.tv_total_score);
        scoreView.setTransitionName("score_transition");
        startPostponedEnterTransition();

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        int completionTime = getIntent().getIntExtra("COMPLETION_TIME", 0);

        tvCompletionTime = findViewById(R.id.tv_completion_time);
        tvCorrectPairs = findViewById(R.id.tv_correct_pairs);
        tvWrongPairs = findViewById(R.id.tv_wrong_pairs);
        tvTotalScore = findViewById(R.id.tv_total_score);
        btnRetryQuiz = findViewById(R.id.btn_retry_quiz);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        // Hiển thị thời gian hoàn thành
        int minutes = completionTime / 60;
        int seconds = completionTime % 60;
        tvCompletionTime.setText(String.format("%d:%02d", minutes, seconds));
        
        // Hiển thị số cặp đúng và số cặp sai
        int correctPairs = score / 10; // Mỗi cặp đúng được 10 điểm
        tvCorrectPairs.setText(String.valueOf(correctPairs));
        tvWrongPairs.setText(String.valueOf(totalQuestions - correctPairs));
        
        // Hiển thị tổng điểm
        tvTotalScore.setText(String.valueOf(score));

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