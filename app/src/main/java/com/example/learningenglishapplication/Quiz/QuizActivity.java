package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

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

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            clickedButton.setBackgroundColor(Color.GREEN);
        } else {
            clickedButton.setBackgroundColor(Color.RED);
            // Tìm và highlight đáp án đúng
            for (Button btn : answerButtons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(Color.GREEN);
                }
            }
        }

        currentQuestionIndex++;

        // Đợi một chút rồi chuyển sang câu tiếp theo
        handler.postDelayed(this::loadNextQuestion, 1500); // 1.5 giây
    }

    private void resetButtons() {
        for (Button btn : answerButtons) {
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.parseColor("#6200EE")); // Màu primary
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());
        startActivity(intent);
        finish(); // Đóng màn hình quiz
    }
}
