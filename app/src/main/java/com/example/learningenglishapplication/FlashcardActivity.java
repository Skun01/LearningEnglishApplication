package com.example.learningenglishapplication;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.model.Vocabulary;

import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private List<Vocabulary> vocabularyList;
    private int currentCardIndex = 0;

    // Views for the flashcard
    private FrameLayout flashcardContainer;
    private LinearLayout cardFront, cardBack;
    private TextView tvFlashcardWord, tvFlashcardMeaning;

    // Control buttons
    private Button btnPrev, btnNext;

    // Animation
    private Animator flipOutAnimator;
    private Animator flipInAnimator;
    private boolean isFront = true;
    private boolean isFlipping = false; // Ngăn click liên tục khi animation chưa kết thúc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        // Lấy danh sách từ vựng đã được gửi qua
        Intent intent = getIntent();
        vocabularyList = (List<Vocabulary>) intent.getSerializableExtra("VOCAB_LIST");

        // Kiểm tra an toàn
        if (vocabularyList == null || vocabularyList.isEmpty()) {
            Toast.makeText(this, "Không có từ vựng để ôn tập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ views
        flashcardContainer = findViewById(R.id.fl_flashcard_container);
        cardFront = findViewById(R.id.ll_card_front);
        cardBack = findViewById(R.id.ll_card_back);
        tvFlashcardWord = findViewById(R.id.tv_flashcard_word);
        tvFlashcardMeaning = findViewById(R.id.tv_flashcard_meaning);
        btnPrev = findViewById(R.id.btn_prev_card);
        btnNext = findViewById(R.id.btn_next_card);

        // Tải animation
        loadAnimations();

        // Hiển thị thẻ đầu tiên
        showCurrentCard();

        // Xử lý sự kiện
        flashcardContainer.setOnClickListener(v -> flipCard());

        btnNext.setOnClickListener(v -> {
            if (currentCardIndex < vocabularyList.size() - 1) {
                currentCardIndex++;
                showCurrentCard();
            } else {
                Toast.makeText(this, "Bạn đã ở từ cuối cùng!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                currentCardIndex--;
                showCurrentCard();
            } else {
                Toast.makeText(this, "Bạn đang ở từ đầu tiên!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAnimations() {
        flipOutAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);
        flipInAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);

        flipOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // Sau khi animation kết thúc, hiển thị mặt còn lại
                if (isFront) {
                    cardFront.setVisibility(View.GONE);
                    cardBack.setVisibility(View.VISIBLE);
                } else {
                    cardBack.setVisibility(View.GONE);
                    cardFront.setVisibility(View.VISIBLE);
                }

                flipInAnimator.start();
                isFront = !isFront;
                isFlipping = false; // Cho phép flip tiếp
            }
        });
    }

    private void showCurrentCard() {
        // Nếu đang ở mặt sau, lật về mặt trước trước khi hiển thị từ mới
        if (!isFront) {
            flipCard();
        }

        Vocabulary currentVocab = vocabularyList.get(currentCardIndex);
        tvFlashcardWord.setText(currentVocab.getWord());
        tvFlashcardMeaning.setText(currentVocab.getMeaning());
    }

    private void flipCard() {
        if (isFlipping) return; // Ngăn spam click khi animation chưa xong
        isFlipping = true;

        flipOutAnimator.setTarget(isFront ? cardFront : cardBack);
        flipInAnimator.setTarget(isFront ? cardBack : cardFront);
        flipOutAnimator.start();
    }
}
