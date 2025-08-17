package com.example.learningenglishapplication.Quiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
 * Multiple choice practice mode inside the Quiz module.
 */
public class LearnModeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvQuestionCounter, tvScore, questionText;
    private ProgressBar progressBar;
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

        tvQuestionCounter = findViewById(R.id.tv_question_counter);
        tvScore = findViewById(R.id.tv_score);
        questionText = findViewById(R.id.question_text);
        progressBar = findViewById(R.id.pb_progress);
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

        // Receive questions from QuizSetupActivity
        List<Vocabulary> questions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        if (questions != null) {
            vocabularyList.addAll(questions);
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
        for (int i = 1; i < 4 && i < vocabularyList.size(); i++) {
            Vocabulary v = vocabularyList.get((currentIndex + i) % vocabularyList.size());
            options.add(v.getMeaning());
        }
        while (options.size() < 4) {
            options.add("?");
        }
        Collections.shuffle(options);
        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));
        tvQuestionCounter.setText(getString(R.string.question_counter_format, currentIndex + 1, vocabularyList.size()));
        tvScore.setText(getString(R.string.score_format, score, vocabularyList.size()));
        progressBar.setProgress((currentIndex * 100) / vocabularyList.size());
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
        tvScore.setText(getString(R.string.final_score_format, score, vocabularyList.size()));
        progressBar.setProgress(100);
    }
}
