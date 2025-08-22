package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;
import com.example.learningenglishapplication.Utils.BaseChildActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeaningQuizActivity extends BaseChildActivity implements View.OnClickListener {

    private TextView tvQuestionCounter, tvScore, tvQuestionMeaning;
    private ProgressBar pbQuizProgress;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private List<Button> answerButtons;

    private List<Vocabulary> quizQuestions;
    private List<Vocabulary> allVocabs;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning_quiz);
        
        // Thiết lập toolbar trực tiếp
        MaterialToolbar toolbar = findViewById(R.id.toolbar_meaning_quiz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.meaning_quiz_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        quizQuestions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        allVocabs = (List<Vocabulary>) getIntent().getSerializableExtra("ALL_VOCABS");

        initViews();
        loadNextQuestion();
    }

    private void initViews() {
        tvQuestionCounter = findViewById(R.id.tv_question_counter);
        tvScore = findViewById(R.id.tv_score);
        tvQuestionMeaning = findViewById(R.id.tv_question_meaning);
        pbQuizProgress = findViewById(R.id.pb_quiz_progress);
        btnAnswer1 = findViewById(R.id.btn_answer_1);
        btnAnswer2 = findViewById(R.id.btn_answer_2);
        btnAnswer3 = findViewById(R.id.btn_answer_3);
        btnAnswer4 = findViewById(R.id.btn_answer_4);

        answerButtons = new ArrayList<>();
        answerButtons.add(btnAnswer1);
        answerButtons.add(btnAnswer2);
        answerButtons.add(btnAnswer3);
        answerButtons.add(btnAnswer4);

        for (Button btn : answerButtons) {
            btn.setOnClickListener(this);
        }
    }

    private void loadNextQuestion() {
        // Nếu đã hết câu hỏi, chuyển sang màn hình kết quả
        if (currentQuestionIndex >= quizQuestions.size()) {
            showResults();
            return;
        }

        // Reset trạng thái các nút cho câu hỏi mới
        resetButtons();

        Vocabulary currentQuestion = quizQuestions.get(currentQuestionIndex);

        // Cập nhật giao diện
        tvQuestionCounter.setText("Câu " + (currentQuestionIndex + 1) + "/" + quizQuestions.size());
        tvScore.setText("Điểm: " + score);
        pbQuizProgress.setProgress((currentQuestionIndex * 100) / quizQuestions.size());
        tvQuestionMeaning.setText(currentQuestion.getMeaning());

        // Tạo các lựa chọn trả lời
        List<String> options = generateOptions(currentQuestion);
        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText(options.get(i));
        }
    }

    private List<String> generateOptions(Vocabulary correctVocab) {
        List<String> options = new ArrayList<>();
        options.add(correctVocab.getWord()); // Thêm đáp án đúng

        // Tạo một danh sách các từ sai
        List<Vocabulary> wrongVocabs = new ArrayList<>(allVocabs);
        wrongVocabs.removeIf(v -> v.getId() == correctVocab.getId()); // Xóa đáp án đúng khỏi danh sách
        Collections.shuffle(wrongVocabs);

        // Thêm 3 đáp án sai
        for (int i = 0; i < 3; i++) {
            options.add(wrongVocabs.get(i).getWord());
        }

        // Trộn các lựa chọn để đáp án đúng không luôn ở vị trí đầu
        Collections.shuffle(options);
        return options;
    }

    @Override
    public void onClick(View v) {
        // Vô hiệu hóa các nút để tránh nhấn nhiều lần
        for (Button btn : answerButtons) {
            btn.setEnabled(false);
        }

        Button clickedButton = (Button) v;
        String selectedAnswer = clickedButton.getText().toString();
        String correctAnswer = quizQuestions.get(currentQuestionIndex).getWord();

        // Tìm nút có đáp án đúng
        Button correctButton = null;
        for (Button btn : answerButtons) {
            if (btn.getText().toString().equals(correctAnswer)) {
                correctButton = btn;
                break;
            }
        }

        if (selectedAnswer.equals(correctAnswer)) {
            // Đáp án đúng - Đặt màu xanh lá cho nút được chọn
            score++;
            clickedButton.setBackgroundResource(R.drawable.button_correct_answer);
            clickedButton.setTextColor(Color.WHITE);
        } else {
            // Đáp án sai - Đặt màu đỏ cho nút được chọn
            clickedButton.setBackgroundResource(R.drawable.button_wrong_answer);
            clickedButton.setTextColor(Color.WHITE);

            // Highlight đáp án đúng với màu xanh lá
            if (correctButton != null) {
                correctButton.setBackgroundResource(R.drawable.button_correct_answer);
                correctButton.setTextColor(Color.WHITE);
            }
        }

        // Đặt các nút còn lại (không phải đáp án đúng và không phải nút được chọn)
        // về màu mặc định với độ mờ
        for (Button btn : answerButtons) {
            if (btn != clickedButton && btn != correctButton) {
                btn.setBackgroundResource(R.drawable.button_ripple_effect);
                btn.setTextColor(getResources().getColor(R.color.text_color_secondary, getTheme()));
                btn.setAlpha(0.6f); // Làm mờ các nút không liên quan
            }
        }

        // Tăng thời gian hiển thị kết quả để người dùng có thể thấy rõ màu sắc
        handler.postDelayed(() -> {
            currentQuestionIndex++;
            loadNextQuestion();
        }, 2500); // Giảm xuống 2.5 giây cho trải nghiệm tốt hơn
    }

    private void resetButtons() {
        for (Button btn : answerButtons) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.button_ripple_effect);
            btn.setTextColor(getResources().getColor(R.color.text_color_primary, getTheme()));
            btn.setAlpha(1.0f); // Đặt lại độ trong suốt về bình thường
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());
        
        // Tạo shared element transition cho score
        View scoreView = findViewById(R.id.tv_score);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair.create(scoreView, "score_transition")
        );
        
        ActivityTransitionManager.startActivityWithExtras(this, intent, options.toBundle());
    }
    
    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}