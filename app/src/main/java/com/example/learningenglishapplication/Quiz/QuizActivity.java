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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends BaseChildActivity implements View.OnClickListener {

    private TextView tvQuestionCounter, tvScore, tvQuestionWord;
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
        setContentView(R.layout.activity_quiz);
        
        // Thiết lập toolbar với tiêu đề "Quiz"
        MaterialToolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.quiz_title));
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
        tvQuestionWord = findViewById(R.id.tv_question_word);
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
            // Thêm hiệu ứng ripple khi nhấn
            btn.setBackgroundResource(R.drawable.button_ripple_effect);
        }
    }

    private void loadNextQuestion() {
        // Nếu đã hết câu hỏi, chuyển sang màn hình kết quả
        if (currentQuestionIndex >= quizQuestions.size()) {
            showResults();
            return;
        }

        resetButtons();

        Vocabulary currentQuestion = quizQuestions.get(currentQuestionIndex);

        // Cập nhật giao diện
        tvQuestionCounter.setText("Câu " + (currentQuestionIndex + 1) + "/" + quizQuestions.size());
        tvScore.setText("Điểm: " + score);
        pbQuizProgress.setProgress((currentQuestionIndex * 100) / quizQuestions.size());
        tvQuestionWord.setText(currentQuestion.getWord());

        // Tạo các lựa chọn trả lời
        List<String> options = generateOptions(currentQuestion);
        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText(options.get(i));
        }
    }

    private List<String> generateOptions(Vocabulary correctVocab) {
        List<String> options = new ArrayList<>();
        options.add(correctVocab.getMeaning()); // Thêm đáp án đúng

        // Tạo một danh sách các từ sai
        List<Vocabulary> wrongVocabs = new ArrayList<>(allVocabs);
        wrongVocabs.removeIf(v -> v.getId() == correctVocab.getId()); // Xóa đáp án đúng khỏi danh sách
        Collections.shuffle(wrongVocabs);

        // Thêm 3 đáp án sai
        for (int i = 0; i < 3; i++) {
            options.add(wrongVocabs.get(i).getMeaning());
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
        String correctAnswer = quizQuestions.get(currentQuestionIndex).getMeaning();

        // Thêm hiệu ứng animation khi chọn đáp án
        clickedButton.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .withEndAction(() -> {
                    clickedButton.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .start();
                })
                .start();

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            // Sử dụng drawable với màu đúng
            clickedButton.setBackgroundResource(R.drawable.button_correct_answer);
            clickedButton.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else {
            // Sử dụng drawable với màu sai
            clickedButton.setBackgroundResource(R.drawable.button_wrong_answer);
            clickedButton.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            // Tìm và highlight đáp án đúng
            for (Button btn : answerButtons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundResource(R.drawable.button_correct_answer);
                    btn.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                }
            }
        }

        currentQuestionIndex++;

        // Đợi một chút rồi chuyển sang câu tiếp theo với hiệu ứng fade
        handler.postDelayed(() -> {
            // Thêm hiệu ứng fade out trước khi load câu hỏi mới
            View questionContainer = findViewById(R.id.question_container);
            questionContainer.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        loadNextQuestion();
                        questionContainer.animate()
                                .alpha(1.0f)
                                .setDuration(300)
                                .start();
                    })
                    .start();
        }, 1500); // 1.5 giây
    }

    private void resetButtons() {
        for (Button btn : answerButtons) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.button_ripple_effect);
            btn.setTextColor(getResources().getColor(R.color.text_color_primary, getTheme()));
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
}
