package com.example.learningenglishapplication.Quiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
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
 * Drag and drop matching mode inside the Quiz module.
 */
public class MatchModeActivity extends AppCompatActivity {

    private LinearLayout wordContainer, meaningContainer;
    private TextView scoreText;
    private final List<Vocabulary> vocabularyList = new ArrayList<>();
    private int score = 0;
    private long userId;
    private StatisticsDataHelper statisticsDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        wordContainer = findViewById(R.id.word_container);
        meaningContainer = findViewById(R.id.meaning_container);
        scoreText = findViewById(R.id.score_text);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        List<Vocabulary> questions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        if (questions != null) {
            vocabularyList.addAll(questions);
        }
        Collections.shuffle(vocabularyList);
        if (vocabularyList.size() > 5) {
            vocabularyList.subList(5, vocabularyList.size()).clear();
        }

        statisticsDataHelper = new StatisticsDataHelper(this);

        setupViews();
        updateScore();
    }

    private void setupViews() {
        for (Vocabulary v : vocabularyList) {
            TextView wordView = createTextView(v.getWord());
            wordView.setTag(v.getMeaning());
            wordView.setOnLongClickListener(view -> {
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                view.startDragAndDrop(null, shadow, view, 0);
                return true;
            });
            wordContainer.addView(wordView);
        }

        List<Vocabulary> shuffled = new ArrayList<>(vocabularyList);
        Collections.shuffle(shuffled);
        for (Vocabulary v : shuffled) {
            TextView meaningView = createTextView(v.getMeaning());
            meaningView.setTag(v.getMeaning());
            meaningView.setOnDragListener((view, event) -> handleDrag(view, event));
            meaningContainer.addView(meaningView);
        }
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 16, 16, 16);
        tv.setBackgroundResource(R.drawable.item_background);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        return tv;
    }

    private boolean handleDrag(View view, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            View dragged = (View) event.getLocalState();
            String from = (String) dragged.getTag();
            String target = (String) view.getTag();
            if (from.equals(target)) {
                dragged.setVisibility(View.INVISIBLE);
                view.setBackgroundColor(0xFFB2FF59); // Green highlight
                score++;
                statisticsDataHelper.logWordLearned(userId);
                updateScore();
                if (score == vocabularyList.size()) {
                    Toast.makeText(this, getString(R.string.final_score_format, score, vocabularyList.size()), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.incorrect, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void updateScore() {
        scoreText.setText(getString(R.string.score_format, score, vocabularyList.size()));
    }
}
