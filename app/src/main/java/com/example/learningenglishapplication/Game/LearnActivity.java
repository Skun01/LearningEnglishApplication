package com.example.learningenglishapplication.Game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
 * Activity luyện tập dạng trắc nghiệm nhiều lựa chọn.
 */
public class LearnActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionText, scoreText;
    private Button option1, option2, option3, option4;
    private List<Vocabulary> vocabularyList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private long userId;
    private StatisticsDataHelper statisticsDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

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

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentIndex >= vocabularyList.size()) {
            showResult();
            return;
        }
        Vocabulary current = vocabularyList.get(currentIndex);
        questionText.setText(current.getWord());

        List<String> options = new ArrayList<>();
        options.add(current.getMeaning());
        for (int i = 1; i < 4; i++) {
            Vocabulary v = vocabularyList.get((currentIndex + i) % vocabularyList.size());
            options.add(v.getMeaning());
        }
        Collections.shuffle(options);
        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));
        scoreText.setText(getString(R.string.score_format, score, vocabularyList.size()));
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String selected = b.getText().toString();
        Vocabulary current = vocabularyList.get(currentIndex);
        if (selected.equals(current.getMeaning())) {
            score++;
            statisticsDataHelper.logWordLearned(userId);
            Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.incorrect, Toast.LENGTH_SHORT).show();
        }
        currentIndex++;
        loadNextQuestion();
    }

    private void showResult() {
        String result = getString(R.string.final_score_format, score, vocabularyList.size());
        scoreText.setText(result);
    }
}
