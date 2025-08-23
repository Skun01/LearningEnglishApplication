package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
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
    private MaterialButton btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private List<MaterialButton> answerButtons;

    private List<Vocabulary> quizQuestions;
    private List<Vocabulary> allVocabs;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.quiz_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        quizQuestions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        allVocabs = (List<Vocabulary>) getIntent().getSerializableExtra("ALL_VOCABS");

        initViews();
        loadNextQuestion();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ActivityTransitionManager.finishWithDefaultTransition(QuizActivity.this);
            }
        });
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

        for (MaterialButton btn : answerButtons) {
            btn.setOnClickListener(this);
            // Thiết lập màu chữ mặc định cho tất cả các nút ngay từ đầu
            btn.setTextColor(Color.WHITE);
        }
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex >= quizQuestions.size()) {
            showResults();
            return;
        }

        resetButtons();

        Vocabulary currentQuestion = quizQuestions.get(currentQuestionIndex);

        tvQuestionCounter.setText("Câu " + (currentQuestionIndex + 1) + "/" + quizQuestions.size());
        tvScore.setText("Điểm: " + score);
        pbQuizProgress.setProgress((currentQuestionIndex * 100) / quizQuestions.size());
        tvQuestionWord.setText(currentQuestion.getWord());

        List<String> options = generateOptions(currentQuestion);
        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText(options.get(i));
        }
    }

    private List<String> generateOptions(Vocabulary correctVocab) {
        List<String> options = new ArrayList<>();
        options.add(correctVocab.getMeaning());

        List<Vocabulary> wrongVocabs = new ArrayList<>(allVocabs);
        wrongVocabs.removeIf(v -> v.getId() == correctVocab.getId());
        Collections.shuffle(wrongVocabs);

        for (int i = 0; i < 3; i++) {
            if (wrongVocabs.size() > i) {
                options.add(wrongVocabs.get(i).getMeaning());
            }
        }

        Collections.shuffle(options);
        return options;
    }

    @Override
    public void onClick(View v) {
        for (MaterialButton btn : answerButtons) {
            btn.setEnabled(false);
        }

        MaterialButton clickedButton = (MaterialButton) v;
        String selectedAnswer = clickedButton.getText().toString();
        String correctAnswer = quizQuestions.get(currentQuestionIndex).getMeaning();

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
            // Thay đổi màu nền của nút đúng thành xanh lá
            clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.correct_answer));
            // Không thay đổi màu chữ vì đã được đặt là màu trắng
        } else {
            // Thay đổi màu nền của nút sai thành đỏ
            clickedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.wrong_answer));

            for (MaterialButton btn : answerButtons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    // Thay đổi màu nền của đáp án đúng thành xanh lá
                    btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.correct_answer));
                }
            }
        }

        currentQuestionIndex++;

        handler.postDelayed(() -> {
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
        }, 1500);
    }

    private void resetButtons() {
        for (MaterialButton btn : answerButtons) {
            btn.setEnabled(true);
            // Khôi phục màu nền về màu xanh dương mặc định
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
            // Giữ nguyên màu chữ là màu trắng
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());

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
}