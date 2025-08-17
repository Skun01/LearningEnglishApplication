package com.example.learningenglishapplication.Quiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Typing test mode inside the Quiz module.
 */
public class TestModeActivity extends AppCompatActivity {

    private TextView tvQuestionCounter, tvScore, questionText;
    private EditText answerInput;
    private Button submitButton;
    private ProgressBar progressBar;
    private final List<Vocabulary> vocabularyList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private long userId;
    private StatisticsDataHelper statisticsDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvQuestionCounter = findViewById(R.id.tv_question_counter);
        tvScore = findViewById(R.id.tv_score);
        questionText = findViewById(R.id.question_text);
        answerInput = findViewById(R.id.answer_input);
        submitButton = findViewById(R.id.submit_button);
        progressBar = findViewById(R.id.pb_progress);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        List<Vocabulary> questions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        if (questions != null) {
            vocabularyList.addAll(questions);
        }
        Collections.shuffle(vocabularyList);

        statisticsDataHelper = new StatisticsDataHelper(this);

        submitButton.setOnClickListener(v -> checkAnswer());
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentIndex >= vocabularyList.size()) {
            showResult();
            return;
        }
        Vocabulary current = vocabularyList.get(currentIndex);
        questionText.setText(current.getMeaning());
        answerInput.setText("");
        tvQuestionCounter.setText(getString(R.string.question_counter_format, currentIndex + 1, vocabularyList.size()));
        tvScore.setText(getString(R.string.score_format, score, vocabularyList.size()));
        progressBar.setProgress((currentIndex * 100) / vocabularyList.size());
    }

    private void checkAnswer() {
        Vocabulary current = vocabularyList.get(currentIndex);
        String answer = answerInput.getText().toString().trim();
        if (answer.equalsIgnoreCase(current.getWord())) {
            score++;
            statisticsDataHelper.logWordLearned(userId);
            Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.correct_answer, current.getWord()), Toast.LENGTH_SHORT).show();
        }
        currentIndex++;
        loadNextQuestion();
    }

    private void showResult() {
        submitButton.setEnabled(false);
        tvScore.setText(getString(R.string.final_score_format, score, vocabularyList.size()));
        progressBar.setProgress(100);
    }
}
