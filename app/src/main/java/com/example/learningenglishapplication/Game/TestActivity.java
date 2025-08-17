package com.example.learningenglishapplication.Game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity kiểm tra dạng nhập từ.
 */
public class TestActivity extends AppCompatActivity {

    private TextView questionText, scoreText;
    private EditText answerInput;
    private Button submitButton;
    private List<Vocabulary> vocabularyList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private long userId;
    private StatisticsDataHelper statisticsDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        answerInput = findViewById(R.id.answer_input);
        submitButton = findViewById(R.id.submit_button);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        VocabularyDataHelper vocabularyDataHelper = new VocabularyDataHelper(new DatabaseHelper(this));
        boolean favoritesOnly = getIntent().getBooleanExtra("FAVORITES_ONLY", false);
        long categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        if (categoryId != -1) {
            vocabularyList = vocabularyDataHelper.getVocabulariesAsList(categoryId);
        } else {
            vocabularyList = vocabularyDataHelper.getVocabulariesForUser(userId, favoritesOnly);
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
        scoreText.setText(getString(R.string.score_format, score, vocabularyList.size()));
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
        scoreText.setText(getString(R.string.final_score_format, score, vocabularyList.size()));
    }
}
